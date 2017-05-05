package serverIntegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

public class ConnectionThread extends Thread {

	private User user;
	private ServerMailbox mailbox;

	private Writer writer;
	private BufferedReader br;

	public ConnectionThread(ServerMailbox mailbox, User user) {
		this.mailbox = mailbox;
		this.user = user;
	}

	public void run() {
		writer = user.getWriter();
		br = user.getBufferedReader();
		getUserName();
		mailbox.addUser(user);
		mailbox.broadcast(user.getName() + " joined."); 	// Sends a message to
														// everyone that this
														// person joined.
		try {
			while (true) {
				String line = br.readLine();
				if (line != null && !line.isEmpty()) {
					if (line.startsWith("M:")) {
						mailbox.broadcast(taggedMessage(line.substring(2)));
					} else if (line.startsWith("E:")) {
						echoMessage(line, writer);
					} else if (line.startsWith("Q")) {
						mailbox.broadcast(user.getName() + " left.");
						mailbox.removeUser(user); // The mailbox should no longer send messages to this user.
						br.close(); // Close the writer and reader.
						writer.close();
						return;
					} else if (line.startsWith("P:")) {
						String name = line.substring(line.indexOf("name=") + 5);
						String message = br.readLine();
						mailbox.sendMessage(name, whisperMessage(message));
					} else {
						errorMessage(line, writer);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String taggedMessage(String message) {
		return user.getName() + ": " + message;
	}
	
	private String whisperMessage(String message) {
		return "[" + user.getName() + "]: " + message;
	}

	private void getUserName() {
		try {
			user.setName(br.readLine());
			while (true) { // Finns redan en användare med det namnet.
				if (user.getName().length() < 3) {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_TOO_SHORT);
				} else if (mailbox.hasUser(user.getName())) {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_TAKEN);
				} else {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_OK);
					return;
				}
				user.setName(br.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void echoMessage(String message, Writer bw) {
		if (message.length() >= 2) {
			ServerMailbox.sendMessage(bw, message.substring(2));
		}
	}

	private void errorMessage(String message, Writer bw) {
		ServerMailbox.sendMessage(bw,
				"Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
						+ " \"E:\" to echo it or \"Q\" to quit.");
	}
}
