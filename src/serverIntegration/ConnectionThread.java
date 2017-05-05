package serverIntegration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			Writer writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			mailbox.addWriter(writer);

			mailbox.broadcast(getName() + " joined"); // Sends a message to
														// everyone that this
														// person joined.
			
			while (true) {
				String line = reader.readLine();
				if (line != null && !line.isEmpty()) {
					System.out.println(line);
					switch (Character.toUpperCase(line.charAt(0))) {
					case 'M':
						mailbox.broadcast(line.substring(2));
						break;
					case 'E':
						echoMessage(line, writer);
						break;
					case 'Q':
						mailbox.broadcast(getName() + " left");
						mailbox.removeWriter(writer); // The mailbox should no
													// longer send messages to
													// this user.
						reader.close(); // Close the writer and reader.
						writer.close();
						return;
					default:
						//errorMessage(line, writer);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void sendMessage(String message, Writer writer) throws IOException {
		writer.write(message + "\r\n");
		writer.flush();
	}

	private void echoMessage(String message, Writer writer) throws IOException {
		if (message.length() >= 2) {
			sendMessage(message.substring(2), writer);
		}
	}
	
	private void errorMessage(String message, Writer writer) throws IOException {
		sendMessage("Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
				+ " \"E:\" to echo it or \"Q\" to quit.", writer);
	}
}
