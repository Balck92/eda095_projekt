package gui;

import java.awt.BorderLayout;
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
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClientGUI {
	
	public static void main(String[] args) {
		new ChatClientGUI();
	}
	
	private Socket s;
	private BufferedWriter writer;
	private BufferedReader reader;
	private JFrame frame = new JFrame("Chat"); // Fönstret

	private JPanel mainPanel = new JPanel();
	private JTextArea messages = new JTextArea();
	
	private JTextField textField2 = new JTextField();
	private JPanel buttonPanel = new JPanel();
	private JButton broadcastButton = new JButton("Broadcast");
	private JButton echoButton = new JButton("Echo");
	private JButton quitButton = new JButton("Quit");

	private Thread readThread;

	public ChatClientGUI() {
		quitButton.addActionListener(new QuitButtonListener());

		buttonPanel.setLayout(new GridLayout(1, 0));
		buttonPanel.add(broadcastButton);
		buttonPanel.add(echoButton);
		buttonPanel.add(quitButton);

		textField2.setText("text2");
		mainPanel.setLayout(null);
		mainPanel.add(messages);
		mainPanel.add(textField2);
		mainPanel.add(buttonPanel);
		messages.setBounds(0, 0, 700, 425);
		textField2.setBounds(0,425,700,150);
		buttonPanel.setBounds(0, 575, 700, 100);

		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null); // Gör så att fönstret hamnar mitt på skärmen
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		
		// Gör så att fönstret anropar quit() när det stängs.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		
		try {
			s = new Socket("localhost", 30000);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			quitButton.addActionListener(new QuitButtonListener());
			broadcastButton.addActionListener(new BroadcastButtonListener());
			echoButton.addActionListener(new EchoButtonListener());

			readThread = new Thread() {
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
			};
			readThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
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

	private class BroadcastButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sendMessage("M:" + textField2.getText());
		}

	}

	private class EchoButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sendMessage("E:" + textField2.getText());
		}

	}

}
