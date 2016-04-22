package ws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
			byte[] bytebuf = new byte[bufsize];
			
			boolean disconnected = false;
			while(!disconnected) {
				
				// Waiting to read from connected client.
				connection.getInputStream().read(bytebuf);
				
				// Parse frame and broadcast if not a disconnect.
				WebSocketMessage receivedMsg = new WebSocketMessage(bytebuf);
				if (!receivedMsg.isDisconnect()) {
					server.broadcast(receivedMsg);					
				} else {
					disconnected = true;
				}
				
			}
			server.removeConnection(connection);
			connection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
