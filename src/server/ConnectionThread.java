package server;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.imageio.ImageIO;

import util.Communication;

// En tr�d k�rs f�r varje anv�ndare som �r uppkopplad till servern.
public class ConnectionThread extends Thread {
	
	private User user;
	private ServerWindow window;

	public ConnectionThread(ChatRoom chatRoom, User user, ServerWindow window) {
		this.user = user;
		this.window = window;
	}

	public void run() {
		// Anv�ndaren m�ste ha ett namn direkt. 
		if (!user.readUserName()) {	// L�s namnet, om anv�ndaren avbryter st�nger vi ner tr�den.
			user.closeConnection();
			return;
		}
		

		receiveMessages();
	}
	
	// Tar emot meddelanden fr�n anv�ndaren till de st�nger ner klienten.
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
					System.out.println("Tog emot bild");
					receiveImage();
				} else {
					errorMessage(line, user.getWriter());	// Skicka ett felmeddelande till anv�ndaren.
					continue;
				}
			}
		}
	}

	private void receiveImage() {
		try {
			InputStream is = user.getInputStream();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			for (int c = is.read(); c != -1; c = is.read()) {
				os.write(c);
			}

			byte[] imageData = os.toByteArray();
			user.getCurrentRoom().broadcastImage(imageData);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	// Den f�rsta raden inneh�ller anv�ndaren man vill skicka till. Den andra raden �r meddelandet.
	private void sendPrivateMessage(String firstLine) {
		firstLine = firstLine.substring(Communication.PRIVATE_MESSAGE.length());
		String name = firstLine.substring(firstLine.indexOf(":") + 1);
		String message = readLine();	// Den andra raden inneh�ller meddelandet.
		if (user.getCurrentRoom().hasUser(name)) {
			Communication.sendMessageToClient(user, sendPrivateMessage(name, message));	// Den som skickar meddelandet f�r upp texten "to [User]: Message"
			user.getCurrentRoom().sendMessage(name, receivePrivateMessage(message));	// Den som tar emot meddelandet f�r upp texten "from [User]: Message"
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
	
	// S�tter anv�ndarens namn framf�r meddelandet.
	private String taggedMessage(String message) {
		return String.format("[%s]: %s", user.getName(), message);
	}
	
	private String receivePrivateMessage(String message) {
		return String.format("from [%s]: %s", user.getName(), message);	// Den andra anv�ndaren tar emot detta meddelandet.
	}
	
	private String sendPrivateMessage(String user, String message) {
		return String.format("to [%s]: %s", user, message);	// Du sj�lv kommer ta emot detta meddelandet.
	}
	
	private void errorMessage(String message, Writer bw) {
		Communication.sendMessage(bw,
				"Message \"" + message + "\" was not sent. Start your message with \"M:\" to broadcast it,"
						+ " \"E:\" to echo it or \"Q\" to quit.");
	}
}
