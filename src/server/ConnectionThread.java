package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import util.Communication;

// En tråd körs för varje användare som är uppkopplad till servern.
public class ConnectionThread extends Thread {
	
	private User user;
	private ServerWindow window;

	public ConnectionThread(ChatRoom chatRoom, User user, ServerWindow window) {
		this.user = user;
		this.window = window;
	}

	public void run() {
		// Användaren måste ha ett namn direkt. 
		if (!user.readUserName()) {	// Läs namnet, om användaren avbryter stänger vi ner tråden.
			user.closeConnection();
			return;
		}

		receiveMessages();
	}
	
	// Tar emot meddelanden från användaren till de stänger ner klienten.
	private void receiveMessages() {
		while (true) {	// Ta emot meddelanden och hantera dem.
			String line = readLine();
			
			if (line != null && !line.isEmpty()) {
				if (line.startsWith(Communication.BROADCAST_MESSAGE)) {
					user.getCurrentRoom().broadcast(taggedMessage(line.substring(Communication.BROADCAST_MESSAGE.length())));
				} else if (line.startsWith(Communication.LEAVE)) {
					user.leaveCurrentRoom();
					user.closeConnection();
					return;
				} else if (line.startsWith(Communication.PRIVATE_MESSAGE)) {
					sendPrivateMessage(line);
				} else if (line.startsWith(Communication.LIST_USERS)) {
					user.getCurrentRoom().listUsersTo(user);
				} else if (line.startsWith(Communication.SEND_IMAGE)) {
					receiveImage(line.substring(Communication.SEND_IMAGE.length()));	// Det efter I:
				} else {
					errorMessage(line, user.getWriter());	// Skicka ett felmeddelande till användaren.
					continue;
				}
			}
		}
	}

	private void receiveImage(String sizeStr) {
		int size = Integer.parseInt(sizeStr);
		try {
			// Läs bytes från klienten.
	        InputStream inputStream = user.getInputStream();
	        byte[] imageData = new byte[size];
	        for (int pos = 0; pos < size; ) {
	        	int bytesRead = inputStream.read(imageData, pos, size - pos);
	        	if (bytesRead == -1) {
	        		System.err.println("Returnerade -1");
	        		return;
	        	}
	        	pos += bytesRead;
	        }
	        
	        // Skicka bilden till alla användare.
			user.getCurrentRoom().broadcastImage(imageData);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Den första raden innehåller användaren man vill skicka till. Den andra raden är meddelandet.
	private void sendPrivateMessage(String firstLine) {
		firstLine = firstLine.substring(Communication.PRIVATE_MESSAGE.length());
		String name = firstLine.substring(firstLine.indexOf(":") + 1);
		String message = readLine();	// Den andra raden innehåller meddelandet.
		if (user.getCurrentRoom().hasUser(name)) {
			Communication.sendMessageToClient(user, sendPrivateMessage(name, message));	// Den som skickar meddelandet får upp texten "to [User]: Message"
			user.getCurrentRoom().sendMessage(name, receivePrivateMessage(message));	// Den som tar emot meddelandet får upp texten "from [User]: Message"
		}
	}
	
	private String readLineNoCatch() throws IOException {
		String mess = user.getBufferedReader().readLine();
		window.setLastMessage(mess);
		return mess;
	}
	
	private String readLine() {
		try {
			return readLineNoCatch();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	// Sätter användarens namn framför meddelandet.
	private String taggedMessage(String message) {
		return String.format("[%s]: %s", user.getName(), message);
	}
	
	private String receivePrivateMessage(String message) {
		return String.format("from [%s]: %s", user.getName(), message);	// Den andra användaren tar emot detta meddelandet.
	}
	
	private String sendPrivateMessage(String user, String message) {
		return String.format("to [%s]: %s", user, message);	// Du själv kommer ta emot detta meddelandet.
	}
	
	private void errorMessage(String message, Writer writer) {
		Communication.sendMessage(writer, "Message \"" + message + "\" was not sent.");
	}
}
