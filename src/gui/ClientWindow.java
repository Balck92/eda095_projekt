package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import gui.multilabel.MessageArea;
import gui.multilabel.MultiLabel;
import gui.multilabel.UserListArea;
import util.ChatUtil;
import util.Communication;
import util.StringPair;

// Klient-fï¿½nstret.
public class ClientWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private ChatClient client;
	
	// Fï¿½nstret
	private JPanel mainPanel = new JPanel();

	// input-rutan.
	private JTextField inputText = new JTextField();
	
	// Meddelandena och anvï¿½ndarna.
	private JPanel upperPanel = new JPanel();
	private MultiLabel messages = new MessageArea(inputText);
	private MultiLabel users = new UserListArea();

	// Knappar
	private JPanel buttonPanel = new JPanel();
	private JButton sendButton = new JButton("Send");
	private JButton sendImageButton = new JButton("Send image");
	private JButton quitButton = new JButton("Quit");
	
	Component[] components = { upperPanel, inputText, sendButton, sendImageButton, quitButton };
	
	public ClientWindow(ChatClient client) {
		this.client = client;
		quitButton.addActionListener(new QuitButtonListener());
		sendImageButton.addActionListener(new SendImageButtonListener());
		sendButton.addActionListener(new SendButtonListener());

		buttonPanel.setLayout(new GridLayout(1, 0)); // Knapparna ligger pï¿½
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
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Lï¿½gger dem under varandra
		mainPanel.add(upperPanel);
		mainPanel.add(inputText);
		mainPanel.add(buttonPanel);
		/*JLabel label = new JLabel("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\nhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkhueigwgkaaaaaaaa");
		JScrollPane scroll = new JScrollPane(label);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 1, 10, 10));
		bottomPanel.add(scroll);
		bottomPanel.setPreferredSize(new Dimension(700, 50));
		mainPanel.add(bottomPanel);*/

		add(mainPanel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowCloser());	// Gï¿½r sï¿½ att fï¿½nstret anropar quit() nï¿½r det stï¿½ngs.
		KeyListener keyl = new KeyboardListener();
		for (Component comp : components) {
			comp.addKeyListener(keyl);
		}
		addComponentListener(new ComponentAdapter() {	// Nï¿½r man ï¿½ndrar storlek anvï¿½nds denna.
			@Override
			public void componentResized(ComponentEvent e) {
				messages.resize();
				users.resize();
			}
		});
	}
	
	private void sendInputText() {
		String text = inputText.getText();
		if (text.toLowerCase().startsWith(Communication.CHAT_PRIVATE_MESSAGE)) {	// Privat meddelande
			StringPair um = ChatUtil.getWhisperNameMessage(text);	// Hï¿½mta namn och meddelande.
			if (um != null) {	// Om det finns ett namn och ett meddelande
				send(Communication.PRIVATE_MESSAGE + um.one + "\r\n", um.two);
			}
		} else if (text.equalsIgnoreCase(Communication.CHAT_LIST_USERS)) {
			send(Communication.LIST_USERS);
		} else {
			send(Communication.BROADCAST_MESSAGE, inputText.getText());
		}
	}
	
	public void send(String message) {
		send("", message);
	}

	// Skickar inget om message ï¿½r tomt.
	public void send(String prefix, String message) {
		inputText.setText("");
		if (!message.isEmpty()) { // Skicka inte tomma meddelanden. 
			client.sendMessage(prefix + message);
		}
	}
	
	public void open() {
		inputText.requestFocus();
		pack();
		setLocationRelativeTo(null); // Lägger fönstret mitt på skärmen
		try {
			EventQueue.invokeAndWait(new Runnable() {	// Skulle förhindra något fel med EventDispatch-tråden
				@Override
				public void run() {
					setVisible(true);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
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

	// Lyssnar på tangentbordet.
	private class KeyboardListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			switch (code) {
			case KeyEvent.VK_ESCAPE:
				client.quit();
				break;
			case KeyEvent.VK_ENTER:
				sendInputText();	// Skicka meddelandet i input-rutan.
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

	// Anvï¿½nds nï¿½r man klickar pï¿½ 'x' uppe till hï¿½ger.
	private class WindowCloser extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			client.quit();
		}

	}
	
	private class SendButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			sendInputText();
		}

	}
	
	private class SendImageButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			    "JPG Images", "jpg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(mainPanel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				client.sendImage(chooser.getSelectedFile());
			}
		}

	}

	private class QuitButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			client.quit();
		}

	}
}
