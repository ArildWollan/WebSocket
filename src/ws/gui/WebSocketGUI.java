package ws.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import ws.WebSocket;
import ws.utils.TimeUtility;

public class WebSocketGUI extends JFrame implements KeyListener {

	private static final long serialVersionUID = -906662423360329760L;
	private JTextArea logArea = new JTextArea();
	private JTextArea connectionsArea = new JTextArea();
	private JTextField commandField = new JTextField();
	private JScrollPane logPane = new JScrollPane(logArea);
	private JScrollPane connectionsPane = new JScrollPane(connectionsArea);
	private WebSocket ws;

	public WebSocketGUI(WebSocket ws) {
		this.ws = ws;
		setTitle("WebSocket");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension logDimension = new Dimension(680, 370);
		Dimension userDimension = new Dimension(240, 370);
		Dimension commandDimension = new Dimension(920, 25);

		Border logBorder = new EmptyBorder(5, 5, 5, 5);
		Border userBorder = new EmptyBorder(5, 0, 5, 5);
		Border commandBorder = new EmptyBorder(0, 5, 5, 5);
		Border lineBorder = new LineBorder(Color.BLACK);

		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		logArea.setEditable(false);
		logArea.setMargin(new Insets(5, 5, 5, 5));
		logArea.append("Server log\n\n");

		logPane.setMinimumSize(logDimension);
		logPane.setMaximumSize(logDimension);
		logPane.setPreferredSize(logDimension);
		logPane.setBorder(new CompoundBorder(logBorder, lineBorder));

		connectionsArea.setLineWrap(true);
		connectionsArea.setWrapStyleWord(true);
		connectionsArea.setEditable(false);
		connectionsArea.setMargin(new Insets(5, 5, 5, 5));
		connectionsArea.append("Active connections\n\n");

		connectionsPane.setMinimumSize(userDimension);
		connectionsPane.setMaximumSize(userDimension);
		connectionsPane.setPreferredSize(userDimension);
		connectionsPane.setBorder(new CompoundBorder(userBorder, lineBorder));

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
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			executeCommand(commandField.getText());
			commandField.setText("");
		}
	}

	public void executeCommand(String command) {
		String timestamp = TimeUtility.getTimeStamp();
		String output = timestamp + " Server: ";

		if (command.equals("q")) {
			System.exit(0);

		} else if (command.equals("help")) {
			output += "Listing all available commands\n" + "q - Terminate WebSocket\n";

		} else if (command.split("\\s+")[0].equals("start")) {
			int port = Integer.parseInt(command.split("\\s+")[1]); 
			output += "Starting WebSocket server at port " + port + "\n";
			this.ws.startServer(port);

		} else if (command.equals("stop")) {
			output += "Stopping WebSocket server\n";
			this.ws.stopServer();

		} else {
			output += command + " is not a recognized command, type 'help' for a list of commands\n";
		}
		logArea.append(output);
	}

	public void addConnection(String connection) {
		String timestamp = TimeUtility.getTimeStamp();
		connectionsArea.append(connection + "\n");
		logArea.append(timestamp + " " + connection + " has connected\n");
	}
}