package ws.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import ws.gui.WebSocketGUI;
import ws.utils.FileHandler;

public class WebServer {

	private WebSocketGUI gui;
	private ServerSocket serverSocket;
	private boolean running;
	private int port;

	/**
	 * Reads the content of an external index file and writes the content to the
	 * PrintWriters output stream.
	 * 
	 * @param pw
	 *            The PrintWriter that will output the content
	 */
	private void sendWebsite(PrintWriter pw) {
		ArrayList<String> website = FileHandler.readFile("txt/index.txt");
		for (String s : website) {
			pw.println(s);
		}
		pw.flush();
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
	 * This method keeps the web server running on the specified port until
	 * canceled. The server will accept new connections and serve each
	 * connection the index page.
	 * 
	 * @param port
	 *            The port number the server will listen at
	 * @exception SocketException
	 *                when the server closes, because server.accept() is
	 *                aborted.
	 * @exception IOException
	 *                can occur for several reasons related to I/O operations.
	 * @see ServerSocket
	 */
	public void runServer(int port) {
		Thread serverThread = new Thread() {
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
					while (running) {
						try (Socket connection = serverSocket.accept()) {
							System.out.println(connection.getInetAddress().getHostAddress() + " has connected");
							BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							PrintWriter pw = new PrintWriter(connection.getOutputStream());

							// Send server headers
							pw.println("HTTP/1.0 200 OK");
							pw.println("Content-Type: text/html");
							pw.println(""); // End of headers

							// Send the HTML page
							sendWebsite(pw);
						} catch (SocketException e) {

						}
					}
				} catch (IOException e) {

				}
			}
		};
		serverThread.start();
	}

	public WebSocketGUI getGUI() {
		return gui;
	}

	public void setGUI(WebSocketGUI gui) {
		this.gui = gui;
	}

	public int getPort() {
		return port;
	}

	public boolean isRunning() {
		return running;
	}
}