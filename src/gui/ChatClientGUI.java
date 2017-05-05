package gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientGUI {

	public static void main(String[] args) {
		new ChatClientGUI("localhost", 30000);
	}

	// Kopplingen till servern
	private Socket s;
	private BufferedWriter writer;
	private BufferedReader reader;

	// Fönstret
	private JFrame frame = new JFrame("Chat");
	private JPanel mainPanel = new JPanel();
	private JTextArea messages = new JTextArea();
	private JTextField textField2 = new JTextField();

	// Knappar
	private JPanel buttonPanel = new JPanel();
	private JButton broadcastButton = new JButton("Broadcast");
	private JButton echoButton = new JButton("Echo");
	private JButton quitButton = new JButton("Quit");

	private Thread readThread; // Tråd som läser input från servern.

	public ChatClientGUI(String host, int port) {
		quitButton.addActionListener(new QuitButtonListener());
		broadcastButton.addActionListener(new BroadcastButtonListener());
		echoButton.addActionListener(new EchoButtonListener());

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger på
														// samma rad
		buttonPanel.add(broadcastButton);
		buttonPanel.add(echoButton);
		buttonPanel.add(quitButton);

		textField2.setText("...");
		messages.setPreferredSize(new Dimension(700, 425));
		textField2.setPreferredSize(new Dimension(700, 150));
		buttonPanel.setPreferredSize(new Dimension(700, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Lägger
																			// dem
																			// under
																			// varandra
		mainPanel.add(messages);
		mainPanel.add(textField2);
		mainPanel.add(buttonPanel);

		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null); // Gör så att fönstret hamnar mitt på
											// skärmen
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Gör så att fönstret anropar quit() när det stängs.
		frame.addWindowListener(new WindowCloser());
		frame.setVisible(true);

		try {
			s = new Socket(host, port);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		readThread = new InputReaderThread();
		readThread.start();
	}

	private void sendMessage(String mess) {
		try {
			writer.write(mess + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void quit() {
		try {
			sendMessage("Q");
			writer.close();
			reader.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			quit();
		}
	}

	private class MessageSendButton {

		public void send(String prefix, String message) {
			if (!message.isEmpty()) { // Skicka inte tomma meddelanden.
				sendMessage(prefix + message);
				textField2.setText("");
			}
		}
	}

	private class BroadcastButtonListener extends MessageSendButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send("M:", textField2.getText());
		}

	}

	private class EchoButtonListener extends MessageSendButton implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send("E:", textField2.getText());
		}

	}

	private class WindowCloser extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			quit();
		}
	}

	// Tråd som läser input från servern.
	private class InputReaderThread extends Thread {
		private LinkedList<String> messageList = new LinkedList<String>();

		public void run() {
			while (true) {
				try {
					String line = reader.readLine();
					if (line != null) {
						if (messageList.size() == 26) {
							messageList.removeFirst();
						}
						messageList.addLast(line);
						StringBuilder displayText = new StringBuilder();
						for (String message : messageList) {
							displayText.append(message + "\n");
						}
						messages.setText(displayText.toString());
					}
				} catch (IOException e) {
					System.exit(0);
				}
			}
		}
	}

}
