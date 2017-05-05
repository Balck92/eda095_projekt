package serverIntegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.SocketException;

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
		if (!getUserName()) {
			quit();
			return;
		}
		mailbox.addUser(user);
		mailbox.broadcast(user.getName() + " joined."); 	// Sends a message to
		// everyone that this

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
						quit();
						return;
					} else if (line.startsWith("P:")) {
						line = line.substring(2);
						String name = line.substring(line.indexOf(":") + 1);
						String message = br.readLine();
						if (mailbox.hasUser(name)) {
							ServerMailbox.sendMessage(user.getWriter(), whisperedMessage(name, message));
							mailbox.sendMessage(name, whisperMessage(message));
						}
					} else if (line.startsWith("L:")) {
						mailbox.listUsersTo(user);
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
		return "from [" + user.getName() + "]: " + message;
	}
	
	private String whisperedMessage(String user, String message) {
		return "to [" + user + "]: " + message;
	}

	private boolean getUserName() {
		try {
			String userName = br.readLine();
			user.setName(userName);
			while (true) { // Finns redan en användare med det namnet.
				if (user.getName().length() < 3) {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_TOO_SHORT);
				} else if (illegalName(userName)) {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_ILLEGAL);
				} else if (mailbox.hasUser(userName)) {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_TAKEN);
				} else {
					ServerMailbox.sendMessage(writer, ChatServer.NAME_OK);
					return true;
				}
				userName = br.readLine();
				user.setName(userName);
			}
		} catch (SocketException e) {	// Hamnar här om användaren trycket "avbryt" när man skriver in namn.
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	
	private void quit() {
		try {
			writer.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private boolean illegalName(String userName) {
		return userName.contains(":");
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
