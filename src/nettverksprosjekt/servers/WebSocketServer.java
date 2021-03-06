package nettverksprosjekt.servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import nettverksprosjekt.gui.GUI;
import nettverksprosjekt.io.WebSocketConnection;
import nettverksprosjekt.io.WebSocketMessage;

public class WebSocketServer {

	private WebSocketServer ws = this;
	private GUI gui;
	private ServerSocket serverSocket;
	private ArrayList<Socket> connections = new ArrayList<>();
	private boolean running;
	private int maxConnections = 100;
	private int port;

	public synchronized ArrayList<Socket> getConnections() {
		return connections;
	}

	/**
	 * Starts the web socket server if it's not already running.
	 * 
	 * @param port
	 *            The port number to listen at
	 * @return True if started successfully, false if already running
	 */
	public boolean startServer(int port) {
		if (!running) {
			running = true;
			this.port = port;
			runServer(port);
			return true;
		}
		return false;
	}

	/**
	 * Stops the web socket server if it's running.
	 * 
	 * @return True if stopped successfully, false if it was not running
	 * @exception IOException
	 *                can occur for several reasons related to I/O operations.
	 */
	public boolean stopServer() {
		if (running) {
			running = false;
			try {
				if (serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method keeps the web socket server running on the specified port
	 * until canceled. The server will accept new connections and add them to an
	 * array of connections as long as the maximum limit is not reached.
	 * Otherwise it will sleep for 5 seconds before attempting to accept a new
	 * connection.
	 * 
	 * @param port
	 *            The port number the server will listen at
	 * @exception SocketException
	 *                when the server closes, because server.accept() is
	 *                aborted.
	 * @exception InteruptedException
	 *                when a thread is waiting, sleeping, or otherwise occupied,
	 *                and the thread is interrupted, either before or during the
	 *                activity.
	 * @exception IOException
	 *                can occur for several reasons related to I/O operations.
	 * @see ServerSocket
	 */
	private void runServer(int port) {
		Thread serverThread = new Thread() {

			public void run() {

				try {
					serverSocket = new ServerSocket(port);

					while (running) {
						if (connections.size() < maxConnections) {
							try {
								Socket connection = serverSocket.accept();
								String address = connection.getInetAddress().getHostAddress();
								connections.add(connection);
								gui.logMessage("SocketServer", address + " has connected");
								gui.updateConnectionsArea();
								new Thread(new WebSocketConnection(connection, ws)).start();
							} catch (SocketException e) {
								
							}
						} else {
							try {
								gui.logMessage("SocketServer", "Max number of connections, sleeping 5 seconds");
								Thread.sleep(5000);
							} catch (InterruptedException e) {

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

	/**
	 * Receives an instance of WebSocketMessage from a client and broadcasts it
	 * to all connected clients.
	 * 
	 * @param message
	 *            The message to broadcast
	 * @exception SocketException
	 *                when a connection is closed by the client or a network
	 *                error occurs. This is usually called a
	 *                BrokenPipeException, but is nested into the
	 *                SocketException in Java
	 * @exception IOException
	 *                can occur for several reasons related to I/O operations.
	 * @see WebSocketMessage
	 */
	synchronized public void broadcast(WebSocketMessage message) {
		for (Socket s : connections) {
			try {
				if (!s.isClosed()) {
					try {
						s.getOutputStream().write(message.getFrame());

					} catch (SocketException e) {
						s.close();
					}
					s.getOutputStream().flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Removes a connection from the list of connections. This method also
	 * notifies WebSocketGUI to update its display.
	 * 
	 * @param connection
	 *            the connection to remove
	 */
	public void removeConnection(Socket connection) {
		connections.remove(connection);
		String address = connection.getInetAddress().getHostAddress();
		gui.logMessage("SocketServer", address + " has disconnected");
		gui.updateConnectionsArea();
	}

	/**
	 * Removes all connections from the list of connections. This method is
	 * usually called prior to closing down the web socket server. This method
	 * also notifies WebSocketGUI to update its display.
	 */
	public void removeAllConnections() {
		if (!connections.isEmpty()) {
			connections.removeAll(connections);
		}
	}

	public GUI getGUI() {
		return gui;
	}

	public void setGUI(GUI gui) {
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

	public boolean isRunning() {
		return running;
	}

	public static void main(String args[]) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				WebServer ws = new WebServer();
				WebSocketServer wss = new WebSocketServer();
				GUI gui = new GUI(ws, wss);

				ws.setGUI(gui);
				wss.setGUI(gui);
			}
		});
	}
}