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
	
	// Meddelanden som klienten får från servern.
	public static final String SHOW_MESSAGE = "S:";
	public static final String USER_JOINED = "UJ:";
	public static final String USER_LEFT = "UL:";
	
	public static void sendMessageToClient(User user, String message) {
		sendMessageToClient(user.getWriter(), message);
	}
	
	private static void sendMessageToClient(Writer writer, String message) {
		try {
			writeMessageToClient(writer, message);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void writeMessageToClient(User user, String message) {
		writeMessageToClient(user.getWriter(), message);
	}
	
	private static void writeMessageToClient(Writer writer, String message) {
		try {
			writer.write(SHOW_MESSAGE + message + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Skicka meddelande att någon gick med.
	public static void sendUserJoinedMessage(User user, String userName) {
		sendMessage(user, Communication.USER_JOINED + userName);
	}
	
	public static void sendUserLeftMessage(User user) {
		sendMessage(user, Communication.USER_LEFT);
	}
	
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
	
	public static void flush(User user) {
		try {
			user.getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
