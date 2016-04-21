package ws.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class WebSocketGUI extends JFrame {

	private static final long serialVersionUID = -906662423360329760L;
	private JTextArea logArea = new JTextArea();
	private JTextArea userArea = new JTextArea();
	private JTextField commandField = new JTextField();
	private JScrollPane logPane = new JScrollPane(logArea);
	private JScrollPane userPane = new JScrollPane(userArea);

	public WebSocketGUI() {
		setTitle("WebSocket");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Dimension logDimension = new Dimension(480, 370);
		Dimension userDimension = new Dimension(240, 370);
		Dimension commandDimension = new Dimension(700, 25);

		Border logBorder = new EmptyBorder(5, 5, 5, 5);
		Border userBorder = new EmptyBorder(5, 0, 5, 5);
		Border commandBorder = new EmptyBorder(0, 5, 5, 5);
		Border lineBorder = new LineBorder(Color.BLACK);

		logArea.setLineWrap(true);
		logArea.setWrapStyleWord(true);
		
		logPane.setMinimumSize(logDimension);
		logPane.setMaximumSize(logDimension);
		logPane.setPreferredSize(logDimension);
		logPane.setBorder(new CompoundBorder(logBorder, lineBorder));

		userArea.setLineWrap(true);
		userArea.setWrapStyleWord(true);

		userPane.setMinimumSize(userDimension);
		userPane.setMaximumSize(userDimension);
		userPane.setPreferredSize(userDimension);
		userPane.setBorder(new CompoundBorder(userBorder, lineBorder));

		commandField.setMinimumSize(commandDimension);
		commandField.setMaximumSize(commandDimension);
		commandField.setPreferredSize(commandDimension);
		commandField.setBorder(new CompoundBorder(commandBorder, lineBorder));

		add(logPane, BorderLayout.CENTER);
		add(userPane, BorderLayout.EAST);
		add(commandField, BorderLayout.SOUTH);

		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new WebSocketGUI();
	}
}