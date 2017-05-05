package serverIntegration;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

// Skickar meddelande till klienterna.
public class ServerMailbox {

	// Mappar från namn till Writer.
	private Set<User> users = new HashSet<User>();

	public synchronized boolean addUser(User user) {
		return users.add(user);
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		for (User user : users) {
			Writer writer = user.getWriter();
			try {
				writer.write(message + "\r\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	
	public synchronized boolean hasUser(String name) {
		return users.contains(new User(name));
	}
	
	public synchronized void removeUser(User user) {
		users.remove(user);
	}
	
	public String toString() {
		return users.toString();
	}
}
