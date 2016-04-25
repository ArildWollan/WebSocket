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

import ws.WebSocket;
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

	private WebSocket ws;

	public WebSocketGUI(WebSocket ws) {
		this.ws = ws;
		setTitle("WebSocket");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension logDimension = new Dimension(700, 370);
		Dimension userDimension = new Dimension(240, 370);
		Dimension commandDimension = new Dimension(940, 25);

		Border logBorder = new EmptyBorder(5, 5, 5, 5);
		Border connectionsBorder = new EmptyBorder(5, 0, 5, 5);
		Border commandBorder = new EmptyBorder(0, 5, 5, 5);
		Border lineBorder = new LineBorder(Color.BLACK);

		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		logArea.setMargin(new Insets(5, 5, 5, 5));
		logArea.append("Server log\n\n");
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
		connectionsArea.append("Active connections (0 / " + ws.getMaxConnections() + ")\n\n");

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

	private void executeCommand(String[] commandInfo) {
		String command = commandInfo[0];
		String[] arguments = new String[commandInfo.length - 1];

		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = commandInfo[i + 1];
		}

		if (command.equals("start")) {
			startServer(arguments);

		} else if (command.equals("stop")) {
			stopServer();

		} else if (command.equals("save")) {
			saveLogFile();

		} else if (command.equals("max")) {
			setMaxConnections(arguments);

		} else if (command.equals("help")) {
			logMessage("Server", "Displaying all available commands");
			for (String s : commandList) {
				logMessage("Server", s);
			}

		} else if (command.equals("clear")) {
			logArea.setText("Server log\n\n");

		} else if (command.equals("exit")) {
			stopServer();
			// TODO: Stop web server
			saveLogFile();
			System.exit(0);

		} else {
			logMessage("Server", command + " is not a recognized command, type [help] for a list of commands");
		}
	}

	private void startServer(String[] arguments) {
		if (arguments.length == 0) {
			if (this.ws.startServer(3002)) {
				logMessage("Server", "Web socket server started and ready to accept connections on port 3002");
			} else {
				logMessage("Server", "Web socket server already running on port " + ws.getPort());
			}
		}

		else if (arguments.length == 1) {
			try {
				int port = Integer.parseInt(arguments[0]);
				if (port >= 1024 && port <= 65535) {
					if (ws.startServer(port)) {
						logMessage("Server",
								"Web socket server started and ready to accept connections on port " + port);
					} else {
						logMessage("Server", "Web socket server already running on port " + ws.getPort());
					}
				} else {
					logMessage("Server", "Port number must be in range (1024-65535)");
				}
			} catch (NumberFormatException e) {
				logMessage("Server", "Port number must be an integer");
			}
		} else {
			logMessage("Server", "Invalid number of arguments, the start command takes 0 - 1 arguments");
		}
	}

	private void stopServer() {
		if (this.ws.stopServer()) {
			ws.removeAllConnections();
			updateConnectionsArea();
			logMessage("Server", "Web socket server stopped");
		} else {
			logMessage("Server", "No web socket server running.");
		}
	}

	private void saveLogFile() {
		String destination = "txt/wslog.txt";
		String content = logArea.getText().trim();

		if (FileHandler.saveFile(destination, content)) {
			logMessage("Server", "Log file created at " + destination);
		} else {
			logMessage("Server", "Undefined error upon trying to create log file");
		}
	}

	private void setMaxConnections(String[] arguments) {
		if (arguments.length == 1) {
			try {
				int max = Integer.parseInt(arguments[0]);
				if (max >= 1 && max <= 1000) {
					ws.setMaxClients(max);
					updateConnectionsArea();
					logMessage("Server", "Maximum number of connections set to " + max);
				} else {
					logMessage("Server", "Maximum number of connections must be an integer between 1 and 1000");
				}
			} catch (NumberFormatException e) {
				logMessage("Server", "Maximum number of connections must be an integer between 1 and 1000");
			}
		} else {
			logMessage("Server", "Invalid number of arguments, the max command takes 1 argument");
		}
	}

	private void updateConnectionsArea() {
		int numConnections = ws.getConnections().size();
		int maxConnections = ws.getMaxConnections();
		connectionsArea.setText("Active connections (" + numConnections + " / " + maxConnections + ")\n\n");

		for (int i = 0; i < numConnections; i++) {
			connectionsArea
					.append((i + 1) + ".  " + ws.getConnections().get(i).getInetAddress().getHostAddress() + "\n");
		}
	}

	public void addConnection(String connection) {
		String timestamp = TimeUtility.getTimeStamp();
		logArea.append(timestamp + " " + connection + " has connected\n");
		updateConnectionsArea();
	}

	public void removeConnection(String connection) {
		String timestamp = TimeUtility.getTimeStamp();
		logArea.append(timestamp + " " + connection + " has disconnected\n");
		updateConnectionsArea();

	}

	public void logMessage(String source, String message) {
		String timestamp = TimeUtility.getTimeStamp();
		logArea.append(timestamp + " " + source + ": " + message + "\n");
	}
}