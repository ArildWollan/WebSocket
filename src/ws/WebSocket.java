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
	private WebSocketGUI gui;
	private int MAXCLIENTS = 100;
	
	public synchronized ArrayList<Socket> getConnections() {
		return connections;
	}

	public void startServer(int portnum) {
		running = true;
		runServer(portnum);
	}

	public void stopServer() {
		running = false;

		try {
			if (server != null && !server.isClosed())
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
						if (connections.size() < MAXCLIENTS) {
							try {
								Socket conn = server.accept();
								connections.add(conn);
								gui.addConnection(conn.getInetAddress().getHostAddress());
								new Thread(new WebSocketConnection(conn, mainWebSocketServer)).start();
								System.out.println(conn.getInetAddress().getHostAddress() + " has connected, clients: " + connections.size());
							} catch (SocketException e) {
								e.printStackTrace();
							}							
						} else {
							try {
								//TODO: This is ugly..
								Thread.sleep(5000);
								System.out.println("MAX connections, sleeping 5s");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		serverThread.start();
	}

	public void setGUI(WebSocketGUI gui) {
		this.gui = gui;
	}

	synchronized public void broadcast(WebSocketMessage message) {
		for(Socket s : connections){
			try {
				s.getOutputStream().write(message.getFrame());
			    s.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void removeConnection(Socket connection) {
		connections.remove(connection);
		System.out.println(connection.getInetAddress().getHostAddress() + " has disconnected, clients: " + connections.size());

	}

	public static void main(String args[]) throws IOException {
		WebSocket ws = new WebSocket();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebSocketGUI gui = new WebSocketGUI(ws);
				ws.setGUI(gui);
			}
		});
	}
}