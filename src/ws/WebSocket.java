package ws;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import ws.gui.WebSocketGUI;
import ws.server.Webserver;

public class WebSocket {

	private ServerSocket server;
	private boolean running;
	private ArrayList<Socket> connections = new ArrayList<>();
	private WebSocket mainWebSocketServer = this;
	private WebSocketGUI gui;
	private int maxConnections = 100;
	private int port;
	
	public synchronized ArrayList<Socket> getConnections() {
		return connections;
	}

	public boolean startServer(int port) {
		if(!running) {
			running = true;
			this.port = port;
			runServer(port);
			return true;
		}
		return false;
	}

	public boolean stopServer() {
		if(running) {
			running = false;
			try {
				if (server != null && !server.isClosed()) {
					server.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;		
		} else {
			return false;
		}
	}

	private void runServer(int portnum) {
		Thread serverThread = new Thread() {

			public void run() {

				try {
					server = new ServerSocket(portnum);

					while (running) {
						if (connections.size() < maxConnections) {
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
	
	synchronized public void broadcast(WebSocketMessage message) {
		for(Socket s : connections){
			try {
				if(!s.isClosed()){
					s.getOutputStream().write(message.getFrame());
					s.getOutputStream().flush();					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void removeConnection(Socket connection) {
		connections.remove(connection);
		System.out.println(connection.getInetAddress().getHostAddress() + " has disconnected, clients: " + connections.size());
		gui.removeConnection(connection.getInetAddress().getHostAddress());
	}
	
	public void removeAllConnections() {
		if(!connections.isEmpty()) {
			connections.removeAll(connections);
		}
	}
	
	public WebSocketGUI getGUI() {
		return gui;
	}

	public void setGUI(WebSocketGUI gui) {
		this.gui = gui;
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public void setMaxClients(int maxClients) {
		this.maxConnections = maxClients;
	}

	public int getPort() {
		return port;
	}

	public static void main(String args[]) throws IOException {
		WebSocket ws = new WebSocket();
		new Thread(new Webserver()).start();

		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebSocketGUI gui = new WebSocketGUI(ws);
				ws.setGUI(gui);
			}
		});
	}
}