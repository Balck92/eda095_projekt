package util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import server.User;

public class Communication {

	// Om anv�ndaren skriver i chatten.
	public static final String CHAT_PRIVATE_MESSAGE = "/w ";
	public static final String CHAT_LIST_USERS = "/list";
	
	// Meddelande f�r att servern ska veta vad den ska g�ra.
	public static final String BROADCAST_MESSAGE = "M:";
	public static final String PRIVATE_MESSAGE = "P:";
	public static final String LEAVE = "Q";
	public static final String LIST_USERS = "L:";
	public static final String SEND_IMAGE = "I:";
	
	// Meddelanden som klienten f�r fr�n servern.
	public static final String SHOW_MESSAGE = "S:";
	public static final String USER_JOINED = "UJ:";
	public static final String USER_LEFT = "UL:";
	
	public static void sendMessageToClient(User user, String message) {
		sendMessageToClient(user.getOutputStream(), message);
	}
	
	private static void sendMessageToClient(DataOutputStream os, String message) {
		try {
			writeMessageToClient(os, message);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void writeMessageToClient(User user, String message) {
		writeMessageToClient(user.getOutputStream(), message);
	}
	
	private static void writeMessageToClient(DataOutputStream os, String message) {
		try {
			os.writeUTF(SHOW_MESSAGE + message + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Skicka meddelande att n�gon gick med.
	public static void sendUserJoinedMessage(User user, String userName) {
		sendMessage(user, Communication.USER_JOINED + userName);
	}
	
	public static void sendUserLeftMessage(User user, String userName) {
		sendMessage(user, Communication.USER_LEFT + userName);
	}
	
	public static void sendMessage(User user, String message) {
		sendMessage(user.getOutputStream(), message);
	}
	
	// Skickar bara till en.
	public static void sendMessage(DataOutputStream os, String message) {
		try {
			writeMessage(os, message);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Skriver meddelandet, med anropar inte flush.
	public static void writeMessage(DataOutputStream os, String message) {
		try {
			os.writeUTF(message + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void flush(User user) {
		try {
			user.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static String readLine(DataInputStream is) {
		String s = null;
		try {
			s = readLineNoCatch(is);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return s;
	}
	
	public static String readLineNoCatch(DataInputStream is) throws IOException {
		String s = is.readUTF();
		if (!s.isEmpty() && s.charAt(s.length() - 1) == '\n')
			s = s.substring(0, s.length() - 1);
		return s;
	}
}
