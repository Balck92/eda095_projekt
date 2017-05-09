package gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import server.Server;
import util.Communication;

// Klienten för chatt-programmet.
public class ChatClient {

	private static final String ENTER_HOST_PORT_PROMPT = "Please enter host and port";

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.start();
	}

	// Kopplingen till servern
	private Socket s;
	private InputStream is;
	private OutputStream os;
	private BufferedWriter writer;
	private BufferedReader reader;

	private ClientWindow window = new ClientWindow(this);	// Fönstret.
	private Thread readThread = new InputReaderThread(); // Tråd som läser input från servern.

	public ChatClient() {
		// Frågar användaren efter host och port.
		UserInputWindow userInput = new UserInputWindow();
		userInput.show(ENTER_HOST_PORT_PROMPT);
		
		while (true) {
			try {
				String host = userInput.getHost();
				host = host.isEmpty() ? "localhost" : host;
				int port = userInput.getPort() == 0 ? 30000: userInput.getPort();
				s = new Socket(host, port);
				is = new BufferedInputStream(s.getInputStream());
				os = new BufferedOutputStream(s.getOutputStream());
				writer = new BufferedWriter(new OutputStreamWriter(os));
				reader = new BufferedReader(new InputStreamReader(is));
				break;
			} catch (IOException e) {
				userInput.show("Couldn't connect. " + ENTER_HOST_PORT_PROMPT);
			}
		}
	}

	public void start() {
		getUserName();
		window.open();
		readThread.start();
	}
	
	public void sendImage(BufferedImage image) {
		try {
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        ImageIO.write(image, "jpg", byteArrayOutputStream);

	        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	        System.out.println("Skickar size: " + byteArrayOutputStream.size());
	        os.write(size);
	        os.write(byteArrayOutputStream.toByteArray());
	        os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void getUserName() {
		try {
			String showText = "Please enter your name";
			while (true) {
				String name = JOptionPane.showInputDialog(showText);
				if (name == null)
					System.exit(0);
				Communication.sendMessage(writer, name);
				String response = reader.readLine();
				if (response.startsWith(Server.NAME_OK)) {
					window.setTitle(name + " - Chat");
					return;
				} else if (response.startsWith(Server.NAME_TAKEN)) {
					showText = "Name \"" + name + "\" is taken. Please enter another name";
				} else if (response.startsWith(Server.NAME_TOO_SHORT)) {
					showText = "Name \"" + name + "\" is too short. Please enter another name";
				} else if (response.startsWith(Server.NAME_ILLEGAL)) {
					showText = "Name \"" + name + "\" contains illegal characters. Please enter another name";
				} else {
					System.err.println("Unknown response: " + response);
					System.exit(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void sendMessage(String message) {
		Communication.sendMessage(writer, message);
	}

	// Säger till servern att kliented stängs ner och stänger sen.
	public void quit() {
		try {
			Communication.sendMessage(writer, "Q");
			writer.close();
			reader.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Tråd som läser input från servern.
	private class InputReaderThread extends Thread {

		public void run() {
			while (true) {
				try {
					String line = reader.readLine();
					if (line != null) {
						handleLine(line);
					}
				} catch (IOException e) {
					System.exit(0);
				}
			}
		}
		
		// Tar hand om raden från servern.
		private void handleLine(String line) {
			if (line.startsWith(Communication.SHOW_MESSAGE)) {
				window.addLine(line.substring(Communication.SHOW_MESSAGE.length()));
			} else if (line.startsWith(Communication.USER_JOINED)) {
				window.addUser(line.substring(Communication.USER_JOINED.length()));
			} else if (line.startsWith(Communication.USER_LEFT)) {
				window.removeUser(line.substring(Communication.USER_LEFT.length()));
			} else if (line.startsWith(Communication.SEND_IMAGE)) {
				receiveImage();
			} else {
				System.err.println("Unknown message received from server: " + line);
				//System.exit(1);
			}
		}
	}
	
	private void receiveImage() {
		try {
	        byte[] sizeAr = new byte[4];
	        is.read(sizeAr);
	        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
	        
	        System.out.println("Tog emot size: " + size);

	        byte[] imageData;
	        try {
	        	imageData = new byte[size];
	        } catch (NegativeArraySizeException e) {
	        	return;
	        }
	        
	        int pos;
	        for (pos = 0; pos < size; pos += is.read(imageData, pos, size - pos)) {}
	        
	        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
	        
	        ImageIcon imageIcon = new ImageIcon(image);
	        
	        JFrame frame = new JFrame();
	        JLabel imageLabel = new JLabel(imageIcon);
	        imageLabel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	        imageLabel.setVisible(true);
	        
	        frame.add(imageLabel);
	        frame.pack();
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.setVisible(true);
	        
	        reader.readLine();
	        
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
