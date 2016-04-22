package ws.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Server {
	
	public static void main(String args[]) throws IOException {
		int portnumber = 3002;
		System.out.println("Web server started - Ready to accept connections on port " + portnumber);
		ServerSocket server = new ServerSocket(portnumber);

		while (true) {
			Socket connection = server.accept();
			System.out.println("\n"+connection.getInetAddress().getHostAddress() + " has connected");
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			PrintWriter pw = new PrintWriter(connection.getOutputStream());
			
			ArrayList<String> clientheaders = new ArrayList<>();
			ServerUtils.readClientHeaders(br, clientheaders);
			
			pw.println("HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade");
			pw.println("Sec-WebSocket-Accept: " + ServerUtils.parseAndGetWebsocketAccept(clientheaders));
	        pw.println(""); //Needed, because?
			
			pw.flush();
	        //TODO: read more if needed
	        int bufsize = 65536; // 64K buffer size, larger messages will fail!
			byte[] frame = new byte[bufsize];

			//TODO: Support several clients
			while(true) {
				// Waiting to read from connected client.
				connection.getInputStream().read(frame);
				// Parse the byte array to get the actual payload.
				String message = new String(ServerUtils.getPayloadFromFrame(frame), Charset.forName("UTF-8"));
				// Send the message back to the client and flush.
				connection.getOutputStream().write(ServerUtils.getFrameFromPayload(message));
			    connection.getOutputStream().flush();
			}
			
			// Close connection, we´ll never get here with the loop above.
			// TODO: close connection if anything crashes. Remember Close handshake!
			//connection.close();
		}
	}


}
