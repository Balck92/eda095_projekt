package gui;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import server.Server;
import util.Communication;

// Klienten f�r chatt-programmet.
public class ChatClient {

	private static final String ENTER_HOST_PORT_PROMPT = "Please enter host and port";
	
	public static void main(String[] args) {
		ChatClient client = new ChatClient();
		client.start();
	}

	// Kopplingen till servern
	private InputStream is;
	private OutputStream os;
	private BufferedWriter writer;
	private BufferedReader reader;

	private ClientWindow window = new ClientWindow(this);	// F�nstret.
	private Thread readThread = new InputReaderThread(); // Tr�d som l�ser input fr�n servern.

	public ChatClient() {
		// Fr�gar anv�ndaren efter host och port.
		UserInputWindow userInput = new UserInputWindow();
		userInput.show(ENTER_HOST_PORT_PROMPT);
		
		// Fr�ga anv�ndaren efter en host och port till de anger n�got som g�r att ansluta till.
		while (true) {
			try {
				String host = userInput.getHost();
				host = host.isEmpty() ? "localhost" : host;		// Om man l�mnar host-rutan tom ansluter den till localhost.
				int port = userInput.getPort() == 0 ? 30000: userInput.getPort();	// Default-port �r 30000.
				Socket s = new Socket(host, port);
				is = new BufferedInputStream(s.getInputStream());
				os = new BufferedOutputStream(s.getOutputStream());
				writer = new BufferedWriter(new OutputStreamWriter(os));
				reader = new BufferedReader(new InputStreamReader(is));
				break;
			} catch (IOException e) {
				userInput.show("Couldn't connect. " + ENTER_HOST_PORT_PROMPT);
			}
		}
	}

	public void start() {
		getUserName();
		window.open();
		readThread.start();
	}
	
	private ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
	
	public void sendImage(File imageFile) {
		try {

			BufferedImage image = ImageIO.read(imageFile);
			String[] nameSeparated = imageFile.getName().split("\\.");
			String extension = nameSeparated[nameSeparated.length - 1];
			
			// G�r om bilden till en array av bytes.

			bytesStream.reset();
	        bytesStream = new ByteArrayOutputStream();
	        if (!ImageIO.write(image, extension, bytesStream)) {	// Kunde inte l�sa.
	        	System.err.println("Could not read image " + imageFile.getAbsolutePath());
	        	return;
	        }
	        
	        byte[] imageData = bytesStream.toByteArray();
	        
	        // Skicka storleken och arrayn.
	        //window.send(Communication.BROADCAST_MESSAGE, " ");
			Communication.sendMessage(writer, Communication.SEND_IMAGE + imageData.length);
			//System.out.println("Klienten skickar bild med storlek " + imageData.length);;
	        os.write(imageData);
	        os.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void getUserName() {
		try {
			String showText = "Please enter your name";
			while (true) {
				String name = JOptionPane.showInputDialog(showText);	// Namnet anv�ndaren har valt.
				if (name == null)	// Om de avbryter blir namnet null.
					System.exit(0);
				Communication.sendMessage(writer, name);	// Skicka f�rslag p� namn till servern.
				String response = reader.readLine();		// Serverns svar.
				if (response.startsWith(Server.NAME_OK)) {	// OK namn.
					window.setTitle(name + " - Chat");
					return;
				} else if (response.startsWith(Server.NAME_TAKEN)) {	// N�gon annan har redan namnet.
					showText = "Name \"" + name + "\" is taken. Please enter another name";
				} else if (response.startsWith(Server.NAME_TOO_SHORT)) {	// Namnet �r f�r kort
					showText = "Name \"" + name + "\" is too short. Please enter another name";
				} else if (response.startsWith(Server.NAME_ILLEGAL)) {	// Namnet inneh�ller tecken som ' ' eller '['
					showText = "Name \"" + name + "\" contains illegal characters. Please enter another name";
				} else {
					System.err.println("Unknown response: " + response);
					System.exit(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void sendMessage(String message) {
		Communication.sendMessage(writer, message);
	}

	// S�ger till servern att kliented st�ngs ner och st�nger sen.
	public void quit() {
		try {
			Communication.sendMessage(writer, "Q");
			writer.close();
			reader.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Tr�d som l�ser input fr�n servern.
	private class InputReaderThread extends Thread {
		
		public void run() {
			while (true) {	// L�s input fr�n servern hela tiden.
				try {
					String line = reader.readLine();
					if (line != null) {
						handleLine(line);
					}
				} catch (IOException e) {
					System.exit(0);
				}
			}
		}
		
		// Tar hand om raden fr�n servern.
		private void handleLine(String line) {
			if (line.startsWith(Communication.SHOW_MESSAGE)) {
				window.addLine(line.substring(Communication.SHOW_MESSAGE.length()));
			} else if (line.startsWith(Communication.USER_JOINED)) {
				window.addUser(line.substring(Communication.USER_JOINED.length()));
			} else if (line.startsWith(Communication.USER_LEFT)) {
				window.removeUser(line.substring(Communication.USER_LEFT.length()));
			} else if (line.startsWith(Communication.SEND_IMAGE)) {
				receiveImage(line.substring(Communication.SEND_IMAGE.length()));
			} else {	// Ok�nt meddelande.
				System.err.println("Unknown message received from server: " + line);
			}
		}
	}
	
	// L�gger bilden i ett eget f�nster.
	private void receiveImage(String sizeStr) {
		int size = Integer.parseInt(sizeStr);
		System.out.println("Tog emot storlek: " + sizeStr);
		try {
			// L�s in bilddata.
	        byte[] imageData = new byte[size];
	        for (int pos = 0; pos < size; ) {
	        	int bytesRead = is.read(imageData, pos, size - pos);
	        	if (bytesRead != -1) {
	        		pos += bytesRead;
	        	} else {
	        		System.err.println("Returnerade -1");
	        		return;
	        	}
	        }
	        
	        //System.out.println("Klienten tog emot bild med stolek " + imageData.length);
	        
	        // Skapa bilden.
	        //ByteArrayInputStream bytesStream = new ByteArrayInputStream(imageData);
	        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
	        if (image == null) {
	        	System.err.println("Bild som klient tog mot �r null");
	        } else {
	        	window.addImage(image);
	        }
		} catch (IOException e) {
			window.addLine("Unable to show image.");
		}
	}
	
}
