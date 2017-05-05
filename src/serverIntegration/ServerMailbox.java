package serverIntegration;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

// Skickar meddelande till klienterna.
public class ServerMailbox {

	// Mappar från namn till Writer.
	private Map<String, User> users = new HashMap<String, User>();

	public synchronized boolean addUser(User user) {
		if (users.containsKey(user.getName())) {	// Finns redan.
			return false;
		}
		users.put(user.getName(), user);
		return true;
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		for (User user : users.values()) {
			Writer writer = user.getWriter();
			try {
				writer.write(message + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	
	public void sendMessage(String userName, String message) {
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
