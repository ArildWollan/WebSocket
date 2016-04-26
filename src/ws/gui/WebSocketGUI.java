package ws.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;

import ws.WebSocketServer;
import ws.server.WebServer;
import ws.utils.FileHandler;
import ws.utils.TimeUtility;

public class WebSocketGUI extends JFrame implements KeyListener {

	private static final long serialVersionUID = -906662423360329760L;

	private JTextArea logArea = new JTextArea();
	private JTextArea connectionsArea = new JTextArea();
	private JTextField commandField = new JTextField();
	private JScrollPane logPane = new JScrollPane(logArea);
	private JScrollPane connectionsPane = new JScrollPane(connectionsArea);

	private int selectedCommand = 0;
	private ArrayList<String> commandList = FileHandler.readFile("txt/commands.txt");
	private ArrayList<String> commandHistory = new ArrayList<String>();

	private WebSocketServer wss;
	private WebServer ws;

	/**
	 * Creates a graphical user interface for the application. The interface
	 * includes a command prompt where users can issue command to both the web
	 * server and the web socket server. Output from the console and both
	 * servers are displayed in the one window, and all active connections are
	 * displayed in a separate window.
	 * 
	 * @param ws The web server
	 * @param wss The web socket server
	 */
	public WebSocketGUI(WebServer ws, WebSocketServer wss) {
		this.ws = ws;
		this.wss = wss;
		setTitle("WebSocket");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension logDimension = new Dimension(740, 370);
		Dimension userDimension = new Dimension(240, 370);
		Dimension commandDimension = new Dimension(980, 25);

		Border logBorder = new EmptyBorder(5, 5, 5, 5);
		Border connectionsBorder = new EmptyBorder(5, 0, 5, 5);
		Border commandBorder = new EmptyBorder(0, 5, 5, 5);
		Border lineBorder = new LineBorder(Color.BLACK);

		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		logArea.setMargin(new Insets(5, 5, 5, 5));
		logArea.append("Program log\n\n");
		DefaultCaret logCaret = (DefaultCaret) logArea.getCaret();
		logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		logPane.setMinimumSize(logDimension);
		logPane.setMaximumSize(logDimension);
		logPane.setPreferredSize(logDimension);
		logPane.setBorder(new CompoundBorder(logBorder, lineBorder));

		connectionsArea.setLineWrap(true);
		connectionsArea.setWrapStyleWord(true);
		connectionsArea.setEditable(false);
		connectionsArea.setMargin(new Insets(5, 5, 5, 5));
		connectionsArea.append("Active connections (0 / " + wss.getMaxConnections() + ")\n\n");

		connectionsPane.setMinimumSize(userDimension);
		connectionsPane.setMaximumSize(userDimension);
		connectionsPane.setPreferredSize(userDimension);
		connectionsPane.setBorder(new CompoundBorder(connectionsBorder, lineBorder));

		commandField.setMinimumSize(commandDimension);
		commandField.setMaximumSize(commandDimension);
		commandField.setPreferredSize(commandDimension);
		commandField.setBorder(new CompoundBorder(commandBorder, lineBorder));
		commandField.addKeyListener(this);

		add(logPane, BorderLayout.CENTER);
		add(connectionsPane, BorderLayout.EAST);
		add(commandField, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		commandField.requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// Send command
		if (e.getKeyCode() == KeyEvent.VK_ENTER && commandField.getText().trim().length() > 0) {
			String[] commandInfo = commandField.getText().split("\\s+");
			executeCommand(commandInfo);
			commandHistory.add(commandInfo[0]);
			selectedCommand = commandHistory.size();
			commandField.setText("");

			// Step backward in command history
		} else if (e.getKeyCode() == KeyEvent.VK_UP && selectedCommand > 0) {
			selectedCommand--;
			commandField.setText(commandHistory.get(selectedCommand));

			// Step forward in command history
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN && selectedCommand < commandHistory.size() - 1) {
			selectedCommand++;
			commandField.setText(commandHistory.get(selectedCommand));
		}
	}

	/**
	 * Takes a String[] containing a command and none or more arguments. Then
	 * executes the correct function based on the command and arguments.
	 * 
	 * @param commandInfo
	 *            a String[] containing the command and its arguments
	 */
	private void executeCommand(String[] commandInfo) {
		String command = commandInfo[0];
		String[] arguments = new String[commandInfo.length - 1];

		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = commandInfo[i + 1];
		}

		if (command.equals("socketstart")) {
			startWebSocketServer(arguments);

		} else if (command.equals("socketstop")) {
			stopWebSocketServer();

		} else if (command.equals("webstart")) {
			startWebServer(arguments);

		} else if (command.equals("webstop")) {
			stopWebServer();

		} else if (command.equals("auto")) {
			autoStart();

		} else if (command.equals("save")) {
			saveLogFile();

		} else if (command.equals("max")) {
			setMaxConnections(arguments);

		} else if (command.equals("help")) {
			logMessage("Console", "Displaying all available commands");
			for (String s : commandList) {
				logMessage("Console", s);
			}

		} else if (command.equals("clear")) {
			logArea.setText("Program log\n\n");

		} else if (command.equals("exit")) {
			stopWebSocketServer();
			stopWebServer();
			saveLogFile();
			System.exit(0);

		} else {
			logMessage("Console", command + " is not a recognized command, type [help] for a list of commands");
		}
	}

	/**
	 * Starts the web socket server if it's not already running. Takes an
	 * argument that specifies which port to listen at. If not argument is
	 * specified the server will listen to port 3002 by default.
	 * 
	 * @param arguments
	 *            The port number to listen at
	 */
	private void startWebSocketServer(String[] arguments) {
		if (arguments.length == 0) {
			if (this.wss.startServer(3002)) {
				logMessage("SocketServer", "Web socket server started and ready to accept connections on port 3002");
			} else {
				logMessage("SocketServer", "Web socket server already running on port " + wss.getPort());
			}
		}

		else if (arguments.length == 1) {
			try {
				int port = Integer.parseInt(arguments[0]);
				if (port >= 1024 && port <= 65535) {
					if (wss.startServer(port)) {
						logMessage("SocketServer",
								"Web socket server started and ready to accept connections on port " + port);
					} else {
						logMessage("SocketServer", "Web socket server already running on port " + wss.getPort());
					}
				} else {
					logMessage("SocketServer", "Port number must be in range (1024-65535)");
				}
			} catch (NumberFormatException e) {
				logMessage("SocketServer", "Port number must be an integer");
			}
		} else {
			logMessage("SocketServer", "Invalid number of arguments, the start command takes 0 - 1 arguments");
		}
	}

	/**
	 * Stops the web socket server if it's running.
	 */
	private void stopWebSocketServer() {
		if (this.wss.stopServer()) {
			wss.removeAllConnections();
			updateConnectionsArea();
			logMessage("SocketServer", "Web socket server stopped");
		} else {
			logMessage("SocketServer", "No web socket server running.");
		}
	}

	/**
	 * Starts the web server if it's not already running. Takes an argument that
	 * specifies which port to listen at. If not argument is specified the
	 * server will listen to port 8080 by default.
	 * 
	 * @param arguments
	 *            The port number to listen at
	 */
	private void startWebServer(String[] arguments) {
		if (arguments.length == 0) {
			if (this.ws.startServer(8080)) {
				logMessage("WebServer", "Web server started and ready to accept connections on port 8080");
			} else {
				logMessage("WebServer", "Web server already running on port " + ws.getPort());
			}
		}

		else if (arguments.length == 1) {
			try {
				int port = Integer.parseInt(arguments[0]);
				if (port >= 1024 && port <= 65535) {
					if (ws.startServer(port)) {
						logMessage("WebServer", "Web server started and ready to accept connections on port " + port);
					} else {
						logMessage("WebServer", "Web server already running on port " + ws.getPort());
					}
				} else {
					logMessage("WebServer", "Port number must be in range (1024-65535)");
				}
			} catch (NumberFormatException e) {
				logMessage("WebServer", "Port number must be an integer");
			}
		} else {
			logMessage("WebServer", "Invalid number of arguments, the webstart command takes 0 - 1 arguments");
		}
	}

	/**
	 * Stops the web server if it's running.
	 */
	private void stopWebServer() {
		if (this.ws.stopServer()) {
			logMessage("WebServer", "Web server stopped");
		} else {
			logMessage("WebServer", "No web server running.");
		}
	}

	/**
	 * Simple setup. Starts both servers on their standard ports. If either of
	 * the servers are already running they will be closed and restarted on
	 * their standard port.
	 */
	private void autoStart() {
		if (ws.isRunning()) {
			ws.stopServer();
			logMessage("WebServer", "Web server stopped");
		}

		if (wss.isRunning()) {
			wss.stopServer();
			logMessage("SocketServer", "Web socket server stopped");
		}
		ws.startServer(8080);
		wss.startServer(3002);
		logMessage("WebServer", "Web server started and ready to accept connections on port 8080");
		logMessage("SocketServer", "Web socket server started and ready to accept connections on port 3002");

	}

	/**
	 * Saves the text in the log view to an external file
	 */
	private void saveLogFile() {
		String destination = "txt/wslog.txt";
		String content = logArea.getText().trim();

		if (FileHandler.saveFile(destination, content)) {
			logMessage("Console", "Log file created at " + destination);
		} else {
			logMessage("Console", "Undefined error upon trying to create log file");
		}
	}

	/**
	 * Sets the maximum number of connections accepted by the web socket server.
	 * 
	 * @param arguments
	 *            The new maximum limit of connections
	 */
	private void setMaxConnections(String[] arguments) {
		if (arguments.length == 1) {
			try {
				int max = Integer.parseInt(arguments[0]);
				if (max >= 1 && max <= 1000) {
					wss.setMaxClients(max);
					updateConnectionsArea();
					logMessage("SocketServer", "Max number of connections set to " + max);
				} else {
					logMessage("SocketServer", "Max number of connections must be an integer between 1 and 1000");
				}
			} catch (NumberFormatException e) {
				logMessage("SocketServer", "Max number of connections must be an integer between 1 and 1000");
			}
		} else {
			logMessage("SocketServer", "Invalid number of arguments, the max command takes 1 argument");
		}
	}

	/**
	 * Updates the connections view. Should be called whenever a connection is
	 * added or removed in WebSocket
	 * 
	 * @see WebSocketServer
	 */
	public void updateConnectionsArea() {
		int numConnections = wss.getConnections().size();
		int maxConnections = wss.getMaxConnections();
		connectionsArea.setText("Active connections (" + numConnections + " / " + maxConnections + ")\n\n");

		for (int i = 0; i < numConnections; i++) {
			String connectionAddress = wss.getConnections().get(i).getInetAddress().getHostAddress();
			connectionsArea.append((i + 1) + ":  " + connectionAddress + "\n");
		}
	}

	/**
	 * Displays the source and content of the message in the log view.
	 * 
	 * @param source
	 *            The source of the message.
	 * @param message
	 *            The message content
	 */
	public void logMessage(String source, String message) {
		String timestamp = TimeUtility.getTimeStamp();
		logArea.append(timestamp + " " + source + ": " + message + "\n");
	}
}