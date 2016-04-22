package ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ClientConnection {
	private Socket connection;
	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;
	private PrintWriter pw;
	
	public ClientConnection(Socket connection) throws IOException {
		this.connection = connection;
		
		while (true) {
			System.out.println(connection.getInetAddress().getHostAddress() + " has connected");
			
			is = connection.getInputStream();
			isr = new InputStreamReader(connection.getInputStream());
			br = new BufferedReader(isr);
			pw = new PrintWriter(connection.getOutputStream());

			// Read headers

			String line = ".";
			String key = "";
			String header = "";

			while (!line.equals("")) {
				line = br.readLine();
				System.out.println(line);

				// Check for key
				header = line.split(":")[0];
				
				if (header.equals("Sec-WebSocket-Key")) {
					key = line.split(":")[1].trim();
					System.out.println(key);
					handshake(encodeKey(key));
				}
			}
			
			pw.flush();
			
			while(true){
				// Temp: To keep server from closing
			}
		}
	}
	
	private void handshake(String encodedKey) {
		pw.println("HTTP/1.1 101 Switching Protocols");
		pw.println("Upgrade: websocket");
		pw.println("Connection: Upgrade");
		pw.println("Sec-WebSocket-Accept: " + encodedKey);
		pw.println(""); // End of headers
		pw.flush();
	}

	private String encodeKey(String key) {
		key += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			String encodedKey = Base64.getEncoder().encodeToString(md.digest(key.getBytes()));
			System.out.println("Encoded key: " + encodedKey);
			return encodedKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
