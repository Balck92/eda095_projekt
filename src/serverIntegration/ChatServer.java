package serverIntegration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread {
	
	public static final String NAME_TAKEN = "name taken";
	public static final String NAME_OK = "name ok";
	public static final String NAME_TOO_SHORT = "name short";
	public static final String NAME_ILLEGAL = "name illegal";

	private int port;
	private ServerSocket ss;
	private ServerMailbox mailbox = new ServerMailbox();

	public static void main(String[] args) {
		new ChatServer(30000).start();
	}

	public ChatServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		ServerWindow window = new ServerWindow(400, 260, port);
		window.start();

		new Thread() {
			public void run() {
				try {
					while (true) {
						Socket s = ss.accept();		// En ny användare anslöt.
						User user = new User(s);
						new ConnectionThread(mailbox, user).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}
}
