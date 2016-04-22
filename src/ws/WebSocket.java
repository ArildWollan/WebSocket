package ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import ws.gui.WebSocketGUI;

public class WebSocket {

	private ServerSocket server;
	private boolean running;
	private ArrayList<Socket> connections = new ArrayList<>();
	private WebSocket mainWebSocketServer = this;
	
	
	public synchronized ArrayList<Socket> getConnections(){
		return connections;
	}
	
	public void startServer(int portnum) {
		running = true;
		runServer(portnum);
	}

	public void stopServer() {
		running = false;

		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void runServer(int portnum) {
		System.out.println("In runServer");
		Thread serverThread = new Thread() {

			public void run() {

				try {
					server = new ServerSocket(portnum);

					while (running) {
						System.out.println("Waiting for connections");
						try {
							Socket conn = server.accept();
							connections.add(conn);
				            new Thread(new WebSocketConnection(conn, mainWebSocketServer)).start();
							
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