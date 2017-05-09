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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.multilabel.MessageArea;
import gui.multilabel.MultiLabel;
import gui.multilabel.UserListArea;
import util.ChatUtil;
import util.Communication;
import util.StringPair;

// Klient-f�nstret.
public class ClientWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private ChatClient client;
	
	// F�nstret
	private JPanel mainPanel = new JPanel();

	// input-rutan.
	private JTextField inputText = new JTextField();
	
	// Meddelandena och anv�ndarna.
	private JPanel upperPanel = new JPanel();
	private MultiLabel messages = new MessageArea(inputText);
	private MultiLabel users = new UserListArea();

	// Knappar
	private JPanel buttonPanel = new JPanel();
	private JButton sendButton = new JButton("Send");
	private JButton sendImageButton = new JButton("Send image");
	private JButton quitButton = new JButton("Quit");
	
	SendButtonListener sbl = new SendButtonListener();
	SendImageButtonListener sibl = new SendImageButtonListener();
	QuitButtonListener qbl = new QuitButtonListener();
	
	Component[] components = { upperPanel, inputText, sendButton, sendImageButton, quitButton };
	
	public ClientWindow(ChatClient client) {
		this.client = client;
		quitButton.addActionListener(qbl);
		sendImageButton.addActionListener(sibl);
		sendButton.addActionListener(sbl);

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger p�
														// samma rad
		buttonPanel.add(sendButton);
		buttonPanel.add(sendImageButton);
		buttonPanel.add(quitButton);

		upperPanel.add(messages);
		upperPanel.add(users);
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.LINE_AXIS));
		messages.setPreferredSize(new Dimension(500, 425));
		users.setPreferredSize(new Dimension(200, 425));
		inputText.setPreferredSize(new Dimension(700, 150));
		buttonPanel.setPreferredSize(new Dimension(700, 100));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // L�gger dem under varandra
		mainPanel.add(upperPanel);
		mainPanel.add(inputText);
		mainPanel.add(buttonPanel);

		add(mainPanel);
		pack();
		setLocationRelativeTo(null); // G�r s� att f�nstret hamnar mitt p�
											// sk�rmen
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloser());	// G�r s� att f�nstret anropar quit() n�r det st�ngs.
		KeyListener keyl = new KeyboardListener();
		for (Component comp : components) {
			comp.addKeyListener(keyl);
		}
		addComponentListener(new ComponentAdapter() {	// N�r man �ndrar storlek anv�nds denna.
			@Override
			public void componentResized(ComponentEvent e) {
				messages.resize();
				users.resize();
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
	
	public void addUser(String userName) {
		users.addLine(userName);
	}
	
	public void removeUser(String userName) {
		users.removeLine(userName);
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
			if (text.toLowerCase().startsWith(Communication.CHAT_PRIVATE_MESSAGE)) {	// Privat meddelande
				StringPair um = ChatUtil.getWhisperNameMessage(text);	// H�mta namn och meddelande.
				if (um != null) {	// Om det finns ett namn och ett meddelande
					send(Communication.PRIVATE_MESSAGE + um.one + "\r\n", um.two);
				}
			} else if (text.equalsIgnoreCase(Communication.CHAT_LIST_USERS)) {
				send(Communication.LIST_USERS);
			} else {
				send(Communication.BROADCAST_MESSAGE, inputText.getText());
			}
		}

	}

	// Anv�nds n�r man klickar p� 'x' uppe till h�ger.
	private class WindowCloser extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			client.quit();
		}
	}
	
	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			client.quit();
		}

	}

	private class SendImageButtonListener extends MessageSender implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "JPG Images", "jpg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(mainPanel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					
					client.sendImage(ImageIO.read(chooser.getSelectedFile()));
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

	}

	private class MessageSender {
		
		public void send(String message) {
			send("", message);
		}

		// Skickar inget om message �r tomt.
		public void send(String prefix, String message) {
			inputText.setText("");
			if (!message.isEmpty()) { // Skicka inte tomma meddelanden. 
				client.sendMessage(prefix + message);
			}
		}
	}
}
