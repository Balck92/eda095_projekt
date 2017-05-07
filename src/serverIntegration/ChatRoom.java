package serverIntegration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import util.Communication;

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
		for (String mess : latestMessages) {	// Skicka de senaste meddelandena.
			Communication.writeMessage(user, mess);
		}
		Communication.flush(user);
		return true;
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		if (latestMessages.size() == MAX_MESSAGES_STORED) {
			latestMessages.removeFirst();
		}
		latestMessages.addLast(message);
		for (User user : users.values()) {
			Communication.sendMessage(user, message);
		}
	}
	
	public synchronized void sendMessage(String userName, String message) {
		User user = users.get(userName);
		if (user != null) {
			Communication.sendMessage(user.getWriter(), message);
		}
	}
	
	public synchronized void listUsersTo(User user) {
		Communication.writeMessage(user, "");
		Communication.writeMessage(user, "Userlist:");
		for (String userName : users.keySet()) {
			Communication.writeMessage(user, "  " + userName);
		}
		Communication.writeMessage(user, "");
		Communication.flush(user);
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
