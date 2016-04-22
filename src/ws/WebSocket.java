package ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import ws.gui.WebSocketGUI;

public class WebSocket {

	private ServerSocket server;
	private boolean running;

	public void startServer(int portnum) {
		running = true;
		runServer(portnum);
	}

	public void stopServer() {
		running = false;

		try {
			if(server != null && !server.isClosed())
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runServer(int portnum) {
		Thread serverThread = new Thread() {

			public void run() {

				try {
					server = new ServerSocket(portnum);

					while (running) {
						System.out.println("Waiting for connections");
						try {
							Socket connection = server.accept();
							System.out.println("Connection established! Woooohooo!");
							new ClientConnection(connection);
							
						} catch (SocketException e) {

						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		serverThread.start();
	}

	public static void main(String args[]) throws IOException {
		WebSocket ws = new WebSocket();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new WebSocketGUI(ws);
			}
		});
	}
}