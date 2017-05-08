package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.net.SocketException;

import util.Communication;

public class ConnectionThread extends Thread {
	
	private User user;
	private ChatRoom chatRoom;
	private ServerWindow window;

	private Writer writer;
	private BufferedReader br;

	public ConnectionThread(ChatRoom chatRoom, User user, ServerWindow window) {
		this.chatRoom = chatRoom;
		this.user = user;
		this.window = window;
	}

	public void run() {
		writer = user.getWriter();
		br = user.getBufferedReader();
		
		// Användaren måste ha ett namn direkt. 
		if (!readUserName()) {	// Läs namnet, om användaren avbryter stänger vi ner tråden.
			quit();
			return;
		}
		
		user.setCurrentRoom(chatRoom);	// Gå med i rummet när du har ett namn.
		chatRoom.broadcast(user.getName() + " joined.");	// Berätta för alla att någon gick med.

		receiveMessages();
	}
	
	private void receiveMessages() {
		while (true) {	// Ta emot meddelanden och hantera dem.
			String line = readLine();
			
			if (line != null && !line.isEmpty()) {
				if (line.startsWith(Communication.BROADCAST_MESSAGE)) {
					user.getCurrentRoom().broadcast(taggedMessage(line.substring(Communication.BROADCAST_MESSAGE.length())));
				} else if (line.startsWith(Communication.LEAVE)) {
					chatRoom.broadcast(user.getName() + " left.");
					chatRoom.removeUser(user); // The mailbox should no longer send messages to this user.
					quit();
					return;
				} else if (line.startsWith(Communication.PRIVATE_MESSAGE)) {
					line = line.substring(Communication.PRIVATE_MESSAGE.length());
					String name = line.substring(line.indexOf(":") + 1);
					String message = readLine();
					if (user.getCurrentRoom().hasUser(name)) {
						Communication.sendMessageToClient(user, receivePrivateMessage(name, message));	// Den som skickar meddelandet får upp texten "to [User]: Message"
						user.getCurrentRoom().sendMessage(name, sendPrivateMessage(message));	// Den som tar emot meddelandet får upp texten "from [User]: Message"
					}
				} else if (line.startsWith(Communication.LIST_USERS)) {
					user.getCurrentRoom().listUsersTo(user);
				} else {
					errorMessage(line, writer);
					continue;
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
			while (true) {
				if (userName.length() < 3) {
					Communication.sendMessage(writer, Server.NAME_TOO_SHORT);
				} else if (illegalName(userName)) {
					Communication.sendMessage(writer, Server.NAME_ILLEGAL);
				} else if (chatRoom.hasUser(userName)) {	// Finns redan en användare med det namnet.
					Communication.sendMessage(writer, Server.NAME_TAKEN);
				} else {
					user.setName(userName);
					Communication.sendMessage(writer, Server.NAME_OK);
					return true;
				}
				userName = readLineNoCatch();
			}
		} catch (SocketException e) {	// Användaren stängde av programmet när de valde namn.
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
		Communication.sendMessage(bw,
				"Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
						+ " \"E:\" to echo it or \"Q\" to quit.");
	}
}
