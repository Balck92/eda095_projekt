package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static final String NAME_TAKEN = "name taken";
	public static final String NAME_OK = "name ok";
	public static final String NAME_TOO_SHORT = "name short";
	public static final String NAME_ILLEGAL = "name illegal";

	private int port;
	private ServerSocket ss;
	private ChatRoom chatRoom = new ChatRoom();

	public static void main(String[] args) {
		new Server(30000).run();
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
		
		ServerWindow window = new ServerWindow(800, 300, port);
		window.start();

		new Thread() {
			public void run() {
				try {
					while (true) {
						Socket s = ss.accept();		// En ny användare anslöt.
						User user = new User(s, chatRoom);
						new ConnectionThread(chatRoom, user, window).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}
}
