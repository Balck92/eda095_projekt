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

public class ChatClient {
	
	private static final String ENTER_HOST_PROMPT = "Please enter host or leave empty for localhost";

	public static void main(String[] args) {
		ChatClient client = new ChatClient(30000);
		client.start();
	}
	
	// Kopplingen till servern
	private Socket s;
	private BufferedWriter writer;
	private BufferedReader reader;

	private ClientWindow window = new ClientWindow(this);
	private Thread readThread; // Tråd som läser input från servern.

	public ChatClient(int port) {
		String host = JOptionPane.showInputDialog(ENTER_HOST_PROMPT);
		while (true) {
			try {
				host = host.isEmpty() ? "localhost" : host;
				s = new Socket(host, port);
				writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				break;
			} catch (IOException e) {
				host = JOptionPane.showInputDialog("Cant connect. " + ENTER_HOST_PROMPT);
				if (host == null) System.exit(0);
			}
		}
	}
	
	public void start() {
		try {
			String showText = "Please enter your name";
			while (true) {
				String name = JOptionPane.showInputDialog(showText);
				ServerMailbox.sendMessage(writer, name);
				String response = reader.readLine();
				if (response.startsWith(ChatServer.NAME_OK)) {
					break;
				} else if (response.startsWith(ChatServer.NAME_TAKEN)) {
					showText = "Name \"" + name + "\" is taken. Please enter another name";
				} else {
					System.err.println("Unknown response: " + response);
					System.exit(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		window.show();
		readThread = new InputReaderThread();
		readThread.start();
	}

	public void sendMessage(String mess) {
		try {
			writer.write(mess + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void quit() {
		try {
			sendMessage("Q");
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
