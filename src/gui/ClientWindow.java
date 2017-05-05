package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientWindow {
	
	private ChatClient client;
	private List<String> messageList = new ArrayList<String>();
	
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
	
	Component[] components = { messages, textField2, broadcastButton, echoButton, quitButton };
	
	public ClientWindow(ChatClient client) {
		this.client = client;
		quitButton.addActionListener(new QuitButtonListener());
		broadcastButton.addActionListener(new BroadcastButtonListener());
		echoButton.addActionListener(new EchoButtonListener());

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger på
														// samma rad
		buttonPanel.add(broadcastButton);
		buttonPanel.add(echoButton);
		buttonPanel.add(quitButton);

		messages.setPreferredSize(new Dimension(700, 425));
		textField2.setPreferredSize(new Dimension(700, 150));
		buttonPanel.setPreferredSize(new Dimension(700, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Lägger dem under varandra
		mainPanel.add(messages);
		mainPanel.add(textField2);
		mainPanel.add(buttonPanel);

		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null); // Gör så att fönstret hamnar mitt på
											// skärmen
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Gör så att fönstret anropar quit() när det stängs.
		frame.addWindowListener(new WindowCloser());
		KeyListener keyl = new KeyboardListener();
		for (Component comp : components) {
			comp.addKeyListener(keyl);
		}
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeMessages();
			}
		});
	}
	
	public void show() {
		textField2.requestFocus();
		frame.setVisible(true);
	}
	
	public void addLine(String line) {
		messageList.add(line);
		resizeMessages();
	}
	
	private void resizeMessages() {
		int maxSize = (int) (32.0 / 900.0 * frame.getHeight());
		while (messageList.size() >= 100) {
			messageList.remove(0);
		}
		StringBuilder displayText = new StringBuilder();
		for (int i = Math.max(messageList.size() - maxSize, 0); i < messageList.size(); i++) {
			displayText.append(messageList.get(i) + "\n");
		}
		messages.setText(displayText.toString());
	}
	
	private class KeyboardListener extends MessageSender implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			switch (code) {
			case KeyEvent.VK_ESCAPE:
				client.quit();
				break;
			case KeyEvent.VK_ENTER:
				send("M:", textField2.getText());
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}

	}
	
	private class BroadcastButtonListener extends MessageSender implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send("M:", textField2.getText());
		}

	}

	private class EchoButtonListener extends MessageSender implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send("E:", textField2.getText());
		}

	}

	private class WindowCloser extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			client.quit();
		}
	}
	
	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			client.quit();
		}
		
	}

	private class MessageSender {

		public void send(String prefix, String message) {
			if (!message.isEmpty()) { // Skicka inte tomma meddelanden.
				client.sendMessage(prefix + message);
				textField2.setText("");
			}
		}
	}
}
