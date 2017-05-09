package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import util.Communication;

// Skickar meddelande till klienterna.
public class ChatRoom {

	// Mappar frï¿½n namn till Writer.
	private Map<String, User> users = new HashMap<String, User>();
	private static final int MAX_MESSAGES_STORED = 10;
	private LinkedList<String> latestMessages = new LinkedList<String>();

	public synchronized boolean addUser(User user) {
		if (users.containsKey(user.getName())) {	// Finns redan.
			return false;
		}
		
		for (String userName : users.keySet()) {
			Communication.sendUserJoinedMessage(user, userName);	// Berï¿½tta fï¿½r anvï¿½ndaren vilka som ï¿½r med i rummet.
		}
		
		users.put(user.getName(), user);
		for (String mess : latestMessages) {	// Skicka de senaste meddelandena.
			Communication.writeMessageToClient(user, mess);
		}
		Communication.flush(user);
		
		for (User u : users.values()) {
			Communication.sendUserJoinedMessage(u, user.getName());
		}
		
		return true;
	}
	
	public synchronized void broadcastImage(byte[] imageData) {
		for (User user : users.values()) {
			// Berätta för klienten att de får en bild och skicka storleken på bilden.
			Communication.sendMessage(user, Communication.SEND_IMAGE + imageData.length);
			OutputStream os = user.getOutputStream();
	        try {
	        	os.write(imageData);	// Skicka bilden.
	        	os.flush();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		if (latestMessages.size() == MAX_MESSAGES_STORED) {
			latestMessages.removeFirst();
		}
		latestMessages.addLast(message);
		for (User user : users.values()) {
			Communication.sendMessageToClient(user, message);
		}
	}
	
	public synchronized void sendMessage(String userName, String message) {
		User user = users.get(userName);
		if (user != null) {
			Communication.sendMessageToClient(user, message);
		}
	}
	
	public synchronized void listUsersTo(User user) {
		Communication.writeMessageToClient(user, "");
		Communication.writeMessageToClient(user, "Userlist:");
		for (String userName : users.keySet()) {
			Communication.writeMessageToClient(user, "  " + userName);
		}
		Communication.writeMessageToClient(user, "");
		Communication.flush(user);
	}
	
	public synchronized boolean hasUser(String name) {
		return users.containsKey(name);
	}
	
	public synchronized void removeUser(User user) {
		users.remove(user.getName());
		for (User u : users.values()) {
			Communication.sendUserLeftMessage(u, user.getName());
		}
	}
	
	public String toString() {
		return users.toString();
	}
}
