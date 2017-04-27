package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChatClientGUI {

	private Socket s;
	private BufferedWriter writer;
	private BufferedReader reader;
	private JFrame frame = new JFrame("Chat"); // Fönstret.

	private JPanel mainPanel = new JPanel();
	private JTextField textField = new JTextField();
	private JPanel buttonPanel = new JPanel();
	private JLabel textLabel = new JLabel("Text label");
	private JButton broadcastButton = new JButton("Broadcast");
	private JButton echoButton = new JButton("Echo");
	private JButton quitButton = new JButton("Quit");
	
	private Thread readThread;

	public ChatClientGUI() {
		try {
			s = new Socket("localhost", 30000);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			quitButton.addActionListener(new QuitButtonListener());
			broadcastButton.addActionListener(new BroadcastButtonListener());
			echoButton.addActionListener(new EchoButtonListener());
			
			readThread = new Thread() {
				public void run() {
					while (true) {
						try {
							String line = reader.readLine();
							if (line != null) {
								textLabel.setText(line);
							}
						} catch (IOException e) {
							System.exit(0);
						}
					}
				}
			};
			readThread.start();

			buttonPanel.setLayout(new GridLayout(1, 0));
			buttonPanel.add(broadcastButton);
			buttonPanel.add(echoButton);
			buttonPanel.add(quitButton);

			textField.setText("Text");

			mainPanel.setLayout(new GridLayout(0, 1));
			mainPanel.add(textField);
			mainPanel.add(buttonPanel);
			mainPanel.add(textLabel);
			
			textField.setBounds(0, 0, 500, 150);
			buttonPanel.setBounds(0, 150, 500, 50);
			textLabel.setBounds(0, 250, 500, 50);

			frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
			frame.setSize(new Dimension(500, 300));
			frame.setLocationRelativeTo(null); // Gör så att fönstret hamnar
												// mitt på skärmen
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		new ChatClientGUI();
	}

	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				writer.write("Q\r\n");
				writer.flush();
				writer.close();
				reader.close();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private class BroadcastButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				writer.write("M:" + textField.getText() + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}
	
	private class EchoButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				writer.write("E:" + textField.getText() + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}

}
