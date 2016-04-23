package ws;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

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
			InputStream istream = connection.getInputStream();
			HttpHeader clientheaders = new HttpHeader(istream);

			PrintWriter pw = new PrintWriter(connection.getOutputStream());
			pw.println("HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade");
			pw.println("Sec-WebSocket-Accept: " + ServerUtils.parseAndGetWebsocketAccept(clientheaders));
			pw.println(""); // Needed, because?
			pw.flush();

			boolean disconnected = false;
			while(!disconnected) {
				// Parse frame and broadcast if not a disconnect.
				WebSocketMessage receivedMsg = new WebSocketMessage(istream);

				if (!receivedMsg.isDisconnect()) {
					server.broadcast(receivedMsg);
					server.getGUI().logMessage(connection.getInetAddress().getHostAddress(),
							receivedMsg.getPayloadAsString());
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
