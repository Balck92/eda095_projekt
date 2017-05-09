package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Servern best�r av ett chattrum, en ServerSocket och ett f�nster.
public class Server {
	
	public static final String NAME_TAKEN = "name taken";
	public static final String NAME_OK = "name ok";
	public static final String NAME_TOO_SHORT = "name short";
	public static final String NAME_ILLEGAL = "name illegal";

	private int port;
	private ServerSocket ss;
	private ChatRoom chatRoom = new ChatRoom();
	private ServerWindow window;

	public static void main(String[] args) {
		new Server(80).run();	// Startar en server p� port 30000.
	}

	public Server(int port) {
		this.port = port;
	}

	public void run() {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		window = new ServerWindow(800, 300, port);
		window.show();	// Visar f�nstret.

		new ServerThread().start();
	}
	
	// En tr�d som accepterar nya anv�ndare och l�gger till dem.
	private class ServerThread extends Thread {
		public void run() {
			try {
				while (true) {
					Socket s = ss.accept();		// En ny anv�ndare ansl�t.
					User user = new User(s, chatRoom);
					Thread userThread = new ConnectionThread(chatRoom, user, window);	//  f�r varje anv�ndare.
					userThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
