package nettverksprosjekt.io;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import nettverksprosjekt.servers.WebSocketServer;
import nettverksprosjekt.utils.ServerUtils;
/**
 * WebSocketConnection is intended to run as an own thread.
 * When run, it will get an inputstream from the socket connection,
 * read the http-header from the client and return the header with
 * correct handshake key. It will then enter a loop and wait for a 
 * transmission, for more info @see WebSocketMessage
 * @author henrik
 *
 */
public class WebSocketConnection implements Runnable {
	private Socket connection;
	private WebSocketServer server;
	private String address;

	public WebSocketConnection(Socket connection, WebSocketServer server) {
		this.connection = connection;
		this.server = server;
		this.address = connection.getInetAddress().getHostAddress();
	}

	@Override
	public void run() {
		try {
			InputStream istream = connection.getInputStream();
			HttpHeader clientheaders = new HttpHeader(istream);

			PrintWriter pw = new PrintWriter(connection.getOutputStream());
			pw.println("HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade");
			pw.println("Sec-WebSocket-Accept: " + ServerUtils.parseAndGetWebsocketAccept(clientheaders));
			pw.println(""); // Needed per http spec.
			pw.flush();

			boolean disconnected = false;
			while (!disconnected) {
				// Parse frame and broadcast if not a disconnect.
				WebSocketMessage receivedMsg = new WebSocketMessage(istream);

				if (!receivedMsg.isDisconnect()) {
					server.broadcast(receivedMsg);
					server.getGUI().logMessage(address, receivedMsg.getPayloadAsString());
				} else {
					disconnected = true;
				}

			}
			istream.close();
			server.removeConnection(connection);
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
