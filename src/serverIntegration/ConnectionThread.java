package serverIntegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ConnectionThread extends Thread {

	private ServerMailbox mailbox;
	private Socket s;

	public ConnectionThread(String name, ServerMailbox mailbox, Socket s) {
		super(name);
		this.mailbox = mailbox;
		this.s = s;
	}

	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			mailbox.addWriter(bw);
			mailbox.broadcast(getName() + " joined"); // Sends a message to
														// everyone that this
														// person joined.

			while (true) {
				String line = br.readLine();
				if (line != null && !line.isEmpty()) {
					switch (Character.toUpperCase(line.charAt(0))) {
					case 'M':
						mailbox.broadcast(line.substring(2));
						break;
					case 'E':
						echoMessage(line, bw);
						break;
					case 'Q':
						mailbox.broadcast(getName() + " left");
						mailbox.removeWriter(bw); // The mailbox should no
													// longer send messages to
													// this user.
						br.close(); // Close the writer and reader.
						bw.close();
						return;
					default:
						errorMessage(line, bw);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void echoMessage(String message, BufferedWriter bw) throws IOException {
		if (message.length() >= 2) {
			ServerMailbox.sendMessage(message.substring(2), bw);
		}
	}
	
	private void errorMessage(String message, BufferedWriter bw) throws IOException {
		ServerMailbox.sendMessage("Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
				+ " \"E:\" to echo it or \"Q\" to quit.", bw);
	}
}
