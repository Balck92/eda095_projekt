package gui;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
				os = new BufferedOutputStream(s.getOutputStream());
				writer = new BufferedWriter(new OutputStreamWriter(os));
				reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
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
	
	public void sendImage(File file) {
		try {
			FileInputStream fInput = new FileInputStream(file);
			for (int c = fInput.read(); c != -1; c = fInput.read()) {
				os.write(c);
			}
			os.flush();
			fInput.close();
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
		
		private void handleLine(String line) {
			if (line.startsWith(Communication.SHOW_MESSAGE)) {
				window.addLine(line.substring(Communication.SHOW_MESSAGE.length()));
			} else if (line.startsWith(Communication.USER_JOINED)) {
				window.addUser(line.substring(Communication.USER_JOINED.length()));
			} else if (line.startsWith(Communication.USER_LEFT)) {
				window.removeUser(line.substring(Communication.USER_LEFT.length()));
			} else {
				System.err.println("Unknown message received from server: " + line);
				System.exit(1);
			}
		}
	}

}
