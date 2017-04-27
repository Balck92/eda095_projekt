package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChatClientGUI {

	private Socket s;
	private BufferedWriter writer;
	private JFrame frame = new JFrame("Chat"); // Fönstret.

	private JPanel mainPanel = new JPanel();
	private JTextField textField = new JTextField();
	private JPanel buttonPanel = new JPanel();
	private JButton broadcastButton = new JButton("Broadcast");
	private JButton echoButton = new JButton("Echo");
	private JButton quitButton = new JButton("Quit");

	public ChatClientGUI() {
		try {
			s = new Socket("localhost", 30000);
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			quitButton.addActionListener(new QuitButtonListener());

			buttonPanel.setLayout(new GridLayout(1, 0));
			buttonPanel.add(broadcastButton);
			buttonPanel.add(echoButton);
			buttonPanel.add(quitButton);

			textField.setText("Text");

			mainPanel.setLayout(null);
			mainPanel.add(textField);
			mainPanel.add(buttonPanel);
			textField.setBounds(0, 0, 500, 150);
			buttonPanel.setBounds(0, 150, 500, 150);

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
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

}
