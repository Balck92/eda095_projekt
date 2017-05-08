package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class User {

	private String userName;
	private BufferedReader reader;
	private Writer writer;
	
	private ChatRoom currentRoom;
	
	public User(Socket s) {
		try {
			reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public User(String name) {
		userName = name;
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
		return reader;
	}
	
	public Writer getWriter() {
		return writer;
	}
	
	@Override
	public String toString() {
		return userName;
	}
	
	public void setCurrentRoom(ChatRoom room) {
		if (currentRoom != null) {	// L�mna rummet du �r i.
			currentRoom.removeUser(this);
		}
		currentRoom = room;
		room.addUser(this);
	}
	
	public ChatRoom getCurrentRoom() {
		return currentRoom;
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode();
	}
}
