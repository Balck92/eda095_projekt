package util;

import java.io.IOException;
import java.io.Writer;

import serverIntegration.User;

public class Communication {

	// Om användaren skriver i chatten.
	public static final String CHAT_PRIVATE_MESSAGE = "/w ";
	public static final String CHAT_LIST_USERS = "/list";
	
	// Meddelande för att servern ska veta vad den ska göra.
	public static final String BROADCAST_MESSAGE = "M:";
	public static final String PRIVATE_MESSAGE = "P:";
	public static final String LEAVE = "Q";
	public static final String LIST_USERS = "L:";
	
	public static void sendMessage(User user, String message) {
		sendMessage(user.getWriter(), message);
	}

	// Skickar bara till en.
	public static void sendMessage(Writer writer, String message) {
		try {
			writeMessage(writer, message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Skriver meddelandet, med anropar inte flush.
	public static void writeMessage(Writer writer, String message) {
		try {
			writer.write(message + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void writeMessage(User user, String message) {
		writeMessage(user.getWriter(), message);
	}
	
	public static void flush(User user) {
		try {
			user.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
