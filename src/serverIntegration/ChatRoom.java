package serverIntegration;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// Skickar meddelande till klienterna.
public class ChatRoom {

	// Mappar från namn till Writer.
	private Map<String, User> users = new HashMap<String, User>();
	private static final int MAX_MESSAGES_STORED = 10;
	private LinkedList<String> latestMessages = new LinkedList<String>();

	public synchronized boolean addUser(User user) {
		if (users.containsKey(user.getName())) {	// Finns redan.
			return false;
		}
		users.put(user.getName(), user);
		for (String mess : latestMessages) {
			sendMessage(user, mess);
		}
		return true;
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		if (latestMessages.size() == MAX_MESSAGES_STORED) {
			latestMessages.removeFirst();
		}
		latestMessages.addLast(message);
		for (User user : users.values()) {
			sendMessage(user, message);
		}
	}
	
	public static void sendMessage(User user, String message) {
		sendMessage(user.getWriter(), message);
	}

	// Skickar bara till en.
	public static void sendMessage(Writer bw, String message) {
		try {
			bw.write(message + "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public synchronized void sendMessage(String userName, String message) {
		User user = users.get(userName);
		if (user != null) {
			sendMessage(user.getWriter(), message);
		}
	}
	
	public synchronized void listUsersTo(User user) {
		sendMessage(user, "");
		sendMessage(user, "Userlist:");
		for (String userName : users.keySet()) {
			sendMessage(user, "  " + userName);
		}
		sendMessage(user, "");
	}
	
	public synchronized boolean hasUser(String name) {
		return users.containsKey(name);
	}
	
	public synchronized void removeUser(User user) {
		users.remove(user.getName());
	}
	
	public String toString() {
		return users.toString();
	}
}
