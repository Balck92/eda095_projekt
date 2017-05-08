package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;

import util.Communication;

public class User {

	private String userName;
	private BufferedReader br;
	private Writer writer;
	
	private ChatRoom currentRoom;
	
	public User(Socket s, ChatRoom chatRoom) {
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
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
			String userName = br.readLine();	// Läs namn
			while (true) {
				if (userName.length() < 3) {	// För kort.
					Communication.sendMessage(writer, Server.NAME_TOO_SHORT);
				} else if (illegalName(userName)) {	// Olagliga tecken.
					Communication.sendMessage(writer, Server.NAME_ILLEGAL);
				} else if (currentRoom.hasUser(userName)) {	// Finns redan en användare med det namnet.
					Communication.sendMessage(writer, Server.NAME_TAKEN);
				} else {	// OK namn.
					this.userName = userName;
					joinCurrentRoom();
					return true;
				}
				userName = br.readLine();	// Läs ett nytt namn.
			}
		} catch (SocketException e) {	// Användaren stängde av programmet när de valde namn.
			return false;				// Returnerar false så att klienten vet att den ska stänga av.
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
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof User) {
			return ((User) other).userName.equals(userName);
		}
		return false;
	}
	
	public String getName() {
		return userName;
	}
	
	public void setName(String name) {
		userName = name;
	}
	
	public BufferedReader getBufferedReader() {
		return br;
	}
	
	public Writer getWriter() {
		return writer;
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	public void setCurrentRoom(ChatRoom room) {
		if (currentRoom != null) {	// Lämna rummet du är i.
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
	
	// Berättar för rummet att vi gick med.
	private void joinCurrentRoom() {
		Communication.sendMessage(writer, Server.NAME_OK);
		currentRoom.addUser(this);
		currentRoom.broadcast(userName + " joined.");	// Berätta för alla att någon gick med.
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode();
	}
}
