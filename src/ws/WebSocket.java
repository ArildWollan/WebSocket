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
import java.util.Arrays;
import java.util.Base64;

public class WebSocket {
	private Socket connection;
	private InputStream is;
	private InputStreamReader isr;
	private BufferedReader br;
	private PrintWriter pw;

	public WebSocket() throws IOException {

		while (true) {
			ServerSocket server = new ServerSocket(3006);
			Socket connection = server.accept();
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
					Handshake(encodeKey(key));
				}
			}
			
			pw.flush();
			
			while(true){
				// Wait for input
				byte[] test2 = new byte[128];
				is.read(test2);
				unmaskMessage(test2);
			}

			// Close connection
			//connection.close();
			
		}
	}
	
	private void unmaskMessage(byte[] message) {
		String bitString = createBitString(message);
		String payloadLength = bitString.substring(9, 15);
		System.out.println("Payload string: " + payloadLength);
		
	}
	
	
	
	private String createBitString(byte[] message) {
		String output = "";
		
		for(int i = 0; i < message.length; i++) {
			String bits = ByteToBits(message[i]);
			output += bits;
		}
		return output;
	}

	
	
	private String ByteToBits(byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}

	
	
	private void Handshake(String encodedKey) {
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

	public static void main(String args[]) throws IOException {
		new WebSocket();
	}
}