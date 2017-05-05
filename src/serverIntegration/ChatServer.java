package serverIntegration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer extends Thread {

	private int port;
	private Vector<Socket> sockets = new Vector<Socket>();
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
		
		ServerWindow window = new ServerWindow(300, 500, port);
		window.start();

		new Thread() {
			public void run() {
				try {
					while (true) {
						Socket s = ss.accept();
						sockets.add(s);
						new ConnectionThread(s.getInetAddress().getHostName(), mailbox, s).start();
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}
}
