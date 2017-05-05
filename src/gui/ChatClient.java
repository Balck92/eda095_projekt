package gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import serverIntegration.ChatServer;
import serverIntegration.ServerMailbox;

// Klienten för chatt-programmet.
public class ChatClient {

	private static final String ENTER_HOST_PORT_PROMPT = "Please enter host and port";

	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.start();
	}

	// Kopplingen till servern
	private Socket s;
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
				writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
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
	
	private void getUserName() {
		try {
			String showText = "Please enter your name";
			while (true) {
				String name = JOptionPane.showInputDialog(showText);
				if (name == null)
					System.exit(0);
				ServerMailbox.sendMessage(writer, name);
				String response = reader.readLine();
				if (response.startsWith(ChatServer.NAME_OK)) {
					window.setTitle(name + " - Chat");
					return;
				} else if (response.startsWith(ChatServer.NAME_TAKEN)) {
					showText = "Name \"" + name + "\" is taken. Please enter another name";
				} else if (response.startsWith(ChatServer.NAME_TOO_SHORT)) {
					showText = "Name \"" + name + "\" is too short. Please enter another name";
				} else if (response.startsWith(ChatServer.NAME_ILLEGAL)) {
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
		ServerMailbox.sendMessage(writer, message);
	}

	public void quit() {
		try {
			ServerMailbox.sendMessage(writer, "Q");
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
						window.addLine(line);
					}
				} catch (IOException e) {
					System.exit(0);
				}
			}
		}
	}

}
