package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import util.Communication;


public class User {

	private String userName;
	private DataInputStream is;
	private DataOutputStream os;
	
	private ChatRoom currentRoom;
	
	public User(Socket s, ChatRoom chatRoom) {
		try {
			is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			this.currentRoom = chatRoom;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public User(String name) {
		userName = name;
	}
	
	public boolean readUserName() {
		try {
			String userName = Communication.readLineNoCatch(is);	// L�s namn
			while (true) {
				if (userName.length() < 3) {	// F�r kort.
					Communication.sendMessage(os, Server.NAME_TOO_SHORT);
				} else if (illegalName(userName)) {	// Olagliga tecken.
					Communication.sendMessage(os, Server.NAME_ILLEGAL);
				} else if (currentRoom.hasUser(userName)) {	// Finns redan en anv�ndare med det namnet.
					Communication.sendMessage(os, Server.NAME_TAKEN);
				} else {	// OK namn.
					this.userName = userName;
					joinCurrentRoom();
					return true;
				}
				userName = Communication.readLineNoCatch(is);	// L�s ett nytt namn.
			}
		} catch (SocketException e) {	// Anv�ndaren st�ngde av programmet n�r de valde namn.
			return false;				// Returnerar false s� att klienten vet att den ska st�nga av.
		} catch (IOException e) {		// Annat fel.
			e.printStackTrace();
			System.exit(1);
		}
		return false;	// Annars klagar java, kommer aldrig hit.
	}
	
	// Inga spaces eller hakparenteser.
	public static boolean illegalName(String userName) {
		return userName.contains(" ") || userName.contains("[") || userName.contains("]");
	}
	
	public String getName() {
		return userName;
	}
	
	public void setName(String name) {
		userName = name;
	}
	
	// St�nger Readern och Writern och s�tter dem till null s� att vi inte kan anv�nda dem igen.
	public void closeConnection() {
		try {
			if (is != null) {
				is.close();
				is = null;
			}
			if (os != null) {
				os.close();
				os = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public DataInputStream getInputStream() {
		return is;
	}
	
	public DataOutputStream getOutputStream() {
		return os;
	}
	
	public void setCurrentRoom(ChatRoom room) {
		if (currentRoom != null) {	// L�mna rummet du �r i.
			leaveCurrentRoom();
		}
		currentRoom = room;
		joinCurrentRoom();
	}
	
	public ChatRoom getCurrentRoom() {
		return currentRoom;
	}
	
	public void leaveCurrentRoom() {
		currentRoom.broadcast(userName + " left.");
		currentRoom.removeUser(this); // The mailbox should no longer send messages to this user.
	}
	
	// Ber�ttar f�r rummet att vi gick med.
	private void joinCurrentRoom() {
		Communication.sendMessage(os, Server.NAME_OK);
		currentRoom.addUser(this);
		currentRoom.broadcast(userName + " joined.");	// Ber�tta f�r alla att n�gon gick med.
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			return ((User) other).userName.equals(userName);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode();
	}
}
