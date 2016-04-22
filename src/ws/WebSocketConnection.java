package ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;

import ws.server.ServerUtils;

public class WebSocketConnection implements Runnable {
	private Socket connection;
	private WebSocket server;
	
	public WebSocketConnection(Socket connection, WebSocket server) {
		this.connection = connection;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			System.out.println("\n"+connection.getInetAddress().getHostAddress() + " has connected");
			InputStreamReader isr = new InputStreamReader(this.connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			PrintWriter pw = new PrintWriter(this.connection.getOutputStream());
			
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
				this.connection.getInputStream().read(frame);
				// Parse the byte array to get the actual payload.
				byte[] framebytes = ServerUtils.getPayloadFromFrame(frame);
				
				if (framebytes != null) {
					String message = new String(framebytes, Charset.forName("UTF-8"));
					// Send the message back to all the client and flush.
					// TODO: handle concurrency with something like mutex
					for(Socket s : this.server.getConnections()){
						s.getOutputStream().write(ServerUtils.getFrameFromPayload(message));
					    s.getOutputStream().flush();
					}
					
				} else {
					break;
				}
				
			}
			System.out.println("\n"+connection.getInetAddress().getHostAddress() + " has disconnected");
			connection.close();
			server.getConnections().remove(connection);
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
