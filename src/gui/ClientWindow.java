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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Klient-fönstret.
public class ClientWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final int START_LINES = 20;
	
	private ChatClient client;
	private List<String> messageList = new ArrayList<String>();
	
	// Fönstret
	private JPanel mainPanel = new JPanel();
	//private JTextArea messages = new JTextArea();
	private JPanel messages = new JPanel();
	private List<JLabel> labelList = new ArrayList<JLabel>();
	private JTextField inputText = new JTextField();

	// Knappar
	private JPanel buttonPanel = new JPanel();
	private JButton sendButton = new JButton("Send");
	private JButton quitButton = new JButton("Quit");
	
	BroadcastButtonListener bbl = new BroadcastButtonListener();
	QuitButtonListener qbl = new QuitButtonListener();
	
	Component[] components = { messages, inputText, sendButton, quitButton };
	
	public ClientWindow(ChatClient client) {
		this.client = client;
		quitButton.addActionListener(qbl);
		sendButton.addActionListener(bbl);

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger på
														// samma rad
		buttonPanel.add(sendButton);
		buttonPanel.add(quitButton);

		messages.setLayout(new GridLayout(0, 1));
		for (int i = 0; i < START_LINES; i++) {
			labelList.add(new MessageLabel(inputText));
			messages.add(labelList.get(i));
		}
		messages.setPreferredSize(new Dimension(700, 425));
		inputText.setPreferredSize(new Dimension(700, 150));
		buttonPanel.setPreferredSize(new Dimension(700, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Lägger dem under varandra
		mainPanel.add(messages);
		mainPanel.add(inputText);
		mainPanel.add(buttonPanel);

		add(mainPanel);
		pack();
		setLocationRelativeTo(null); // Gör så att fönstret hamnar mitt på
											// skärmen
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Gör så att fönstret anropar quit() när det stängs.
		addWindowListener(new WindowCloser());
		KeyListener keyl = new KeyboardListener();
		for (Component comp : components) {
			comp.addKeyListener(keyl);
		}
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeMessages();
			}
		});
	}
	
	public void open() {
		inputText.requestFocus();
		setVisible(true);
	}
	
	public void addLine(String line) {
		messageList.add(line);
		resizeMessages();
	}
	
	private void resizeMessages() {
		while (messageList.size() >= 100) {
			messageList.remove(0);
		}
		
		int maxSize = (int) (START_LINES / 700.0 * getHeight());
		maxSize = maxSize > 30 ? 30 : maxSize;	// Max 30 messages.
		if (labelList.size() != maxSize) {
			messages.removeAll();
			while (labelList.size() < maxSize) {
				labelList.add(new MessageLabel(inputText));
			}
			while (labelList.size() > maxSize) {
				labelList.remove(labelList.size() - 1);
			}
			for (JLabel label : labelList) {
				messages.add(label);
			}
		}
		
		for (int i = 0; i < labelList.size(); i++) {
			int index = messageList.size() - labelList.size() + i;
			if (index > 0) {
				labelList.get(i).setText(messageList.get(index));
			}
		}
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
				bbl.actionPerformed(null);
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
			String text = inputText.getText();
			if (text.contains(":")) {
				String name = text.substring(0, text.indexOf(':'));
				String message = text.substring(text.indexOf(':') + 1);
				send("P:name=" + name + "\r\n", message);
			} else {
				send("M:", inputText.getText());
			}
		}

	}

	private class EchoButtonListener extends MessageSender implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send("E:", inputText.getText());
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
			inputText.setText("");
			if (!message.isEmpty()) { // Skicka inte tomma meddelanden.
				client.sendMessage(prefix + message);
			}
		}
	}
}
