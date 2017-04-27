package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChatClientGUI {
	
	private JFrame frame = new JFrame("Chat");	// F�nstret.
	
	private JPanel mainPanel = new JPanel();
	private JTextField textField = new JTextField();
	private JTextField textField2 = new JTextField();
	private JPanel buttonPanel = new JPanel();
	private JButton broadcastButton = new JButton("Broadcast");
	private JButton echoButton = new JButton("Echo");
	private JButton quitButton = new JButton("Quit");
	
	public ChatClientGUI() {
		quitButton.addActionListener(new QuitButtonListener());
		
		buttonPanel.setLayout(new GridLayout(1, 0));
		buttonPanel.add(broadcastButton);
		buttonPanel.add(echoButton);
		buttonPanel.add(quitButton);
		
		textField.setText("Text");
		textField2.setText("Hej");
		mainPanel.setLayout(null);
		mainPanel.add(textField);
		mainPanel.add(textField2);
		mainPanel.add(buttonPanel);
		textField.setBounds(0, 0, 700, 425);
		textField2.setBounds(0,425,700,150);
		buttonPanel.setBounds(0, 575, 700, 100);
		
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.setSize(new Dimension(700, 700));
		frame.setLocationRelativeTo(null); // G�r s� att f�nstret hamnar mitt p� sk�rmen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new ChatClientGUI();
	}
	
	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Skicka meddelande 'Q'
			System.exit(0);
		}
		
	}
}
