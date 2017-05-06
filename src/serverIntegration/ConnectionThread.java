package serverIntegration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.SocketException;

import util.ChatConstants;

public class ConnectionThread extends Thread {
	
	private User user;
	private ChatRoom mailbox;
	private ServerWindow window;

	private Writer writer;
	private BufferedReader br;

	public ConnectionThread(ChatRoom mailbox, User user, ServerWindow window) {
		this.mailbox = mailbox;
		this.user = user;
		this.window = window;
	}

	public void run() {
		writer = user.getWriter();
		br = user.getBufferedReader();
		if (!readUserName()) {	// Läs namnet, om användaren avbryter stänger vi ner tråden.
			quit();
			return;
		}
		mailbox.addUser(user);
		mailbox.broadcast(user.getName() + " joined.");	// Berätta för alla att någon gick med.

		receiveMessages();
	}
	
	private void receiveMessages() {
		while (true) {	// Ta emot meddelanden och hantera dem.
			String line = readLine();
			
			if (line != null && !line.isEmpty()) {
				if (line.startsWith(ChatConstants.BROADCAST_MESSAGE)) {
					mailbox.broadcast(taggedMessage(line.substring(2)));
				} else if (line.startsWith(ChatConstants.LEAVE)) {
					mailbox.broadcast(user.getName() + " left.");
					mailbox.removeUser(user); // The mailbox should no longer send messages to this user.
					quit();
					return;
				} else if (line.startsWith(ChatConstants.PRIVATE_MESSAGE)) {
					line = line.substring(2);
					String name = line.substring(line.indexOf(":") + 1);
					String message = readLine();
					if (mailbox.hasUser(name)) {
						ChatRoom.sendMessage(user.getWriter(), receivePrivateMessage(name, message));
						mailbox.sendMessage(name, sendPrivateMessage(message));
					}
				} else if (line.startsWith(ChatConstants.LIST_USERS)) {
					mailbox.listUsersTo(user);
				} else {
					errorMessage(line, writer);
					break;
				}
			}
		}
	}
	
	private String readLineNoCatch() throws IOException {
		String mess = br.readLine();
		window.setLastMessage(mess);
		return mess;
	}
	
	private String readLine() {
		try {
			return readLineNoCatch();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	private boolean readUserName() {
		try {
			String userName = readLineNoCatch();
			user.setName(userName);
			while (true) {
				if (user.getName().length() < 3) {
					ChatRoom.sendMessage(writer, ChatServer.NAME_TOO_SHORT);
				} else if (illegalName(userName)) {
					ChatRoom.sendMessage(writer, ChatServer.NAME_ILLEGAL);
				} else if (mailbox.hasUser(userName)) {	// Finns redan en användare med det namnet.
					ChatRoom.sendMessage(writer, ChatServer.NAME_TAKEN);
				} else {
					ChatRoom.sendMessage(writer, ChatServer.NAME_OK);
					return true;
				}
				userName = readLineNoCatch();
				user.setName(userName);
			}
		} catch (SocketException e) {	// Användaren stängde av programmet.
			return false;
		} catch (IOException e) {		// Annat fel.
			e.printStackTrace();
			System.exit(1);
		}
		return false;	// Annars klagar java, kommer aldrig hit.
	}
	
	private String taggedMessage(String message) {
		return String.format("[%s]: %s", user.getName(), message);
	}
	
	private String sendPrivateMessage(String message) {
		return String.format("from [%s]: %s", user.getName(), message);	// Den andra användaren tar emot detta meddelandet.
	}
	
	private String receivePrivateMessage(String user, String message) {
		return String.format("to [%s]: %s", user, message);	// Du själv kommer ta emot detta meddelandet.
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
	
	// Inga spaces eller hakparenteser.
	public static boolean illegalName(String userName) {
		return userName.contains(" ") || userName.contains("[") || userName.contains("]");
	}

	private void errorMessage(String message, Writer bw) {
		ChatRoom.sendMessage(bw,
				"Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
						+ " \"E:\" to echo it or \"Q\" to quit.");
	}
}
