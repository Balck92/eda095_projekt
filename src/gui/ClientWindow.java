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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.ChatConstants;
import util.ChatUtil;
import util.StringPair;

// Klient-fönstret.
public class ClientWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private ChatClient client;
	
	// Fönstret
	private JPanel mainPanel = new JPanel();

	// input-rutan.
	private JTextField inputText = new JTextField();
	
	// Meddelandena och användarna.
	private JPanel upperPanel = new JPanel();
	private MultiLabel messages = new MessageArea(inputText);
	//private MultiLabel users = new MultiLabel();

	// Knappar
	private JPanel buttonPanel = new JPanel();
	private JButton sendButton = new JButton("Send");
	private JButton quitButton = new JButton("Quit");
	
	SendButtonListener sbl = new SendButtonListener();
	QuitButtonListener qbl = new QuitButtonListener();
	
	Component[] components = { upperPanel, inputText, sendButton, quitButton };
	
	public ClientWindow(ChatClient client) {
		this.client = client;
		quitButton.addActionListener(qbl);
		sendButton.addActionListener(sbl);

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger på
														// samma rad
		buttonPanel.add(sendButton);
		buttonPanel.add(quitButton);

		upperPanel.add(messages);
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));
		messages.setPreferredSize(new Dimension(700, 425));
		inputText.setPreferredSize(new Dimension(700, 150));
		buttonPanel.setPreferredSize(new Dimension(700, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Lägger dem under varandra
		mainPanel.add(upperPanel);
		mainPanel.add(inputText);
		mainPanel.add(buttonPanel);

		add(mainPanel);
		pack();
		setLocationRelativeTo(null); // Gör så att fönstret hamnar mitt på
											// skärmen
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloser());	// Gör så att fönstret anropar quit() när det stängs.
		KeyListener keyl = new KeyboardListener();
		for (Component comp : components) {
			comp.addKeyListener(keyl);
		}
		addComponentListener(new ComponentAdapter() {	// När man ändrar storlek används denna.
			@Override
			public void componentResized(ComponentEvent e) {
				messages.resize();
			}
		});
	}
	
	public void open() {
		inputText.requestFocus();
		setVisible(true);
	}
	
	public void addLine(String line) {
		messages.addLine(line);
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
				sbl.actionPerformed(null);	// Skicka meddelandet i input-rutan.
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
	
	private class SendButtonListener extends MessageSender implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String text = inputText.getText();
			if (text.toLowerCase().startsWith(ChatConstants.CHAT_PRIVATE_MESSAGE)) {	// Privat meddelande
				StringPair um = ChatUtil.getWhisperNameMessage(text);
				if (um != null) {	// Om det finns ett namn och ett meddelande
					send(ChatConstants.PRIVATE_MESSAGE + um.one + "\r\n", um.two);
				}
			} else if (text.equalsIgnoreCase(ChatConstants.CHAT_LIST_USERS)) {
				send(ChatConstants.LIST_USERS);
			} else {
				send(ChatConstants.BROADCAST_MESSAGE, inputText.getText());
			}
		}

	}

	// Används när man klickar på 'x' uppe till höger.
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
		
		public void send(String message) {
			send("", message);
		}

		// Skickar inget om message är tomt.
		public void send(String prefix, String message) {
			inputText.setText("");
			if (!message.isEmpty()) { // Skicka inte tomma meddelanden.
				client.sendMessage(prefix + message);
			}
		}
	}
}
