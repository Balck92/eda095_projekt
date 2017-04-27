import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoTCP1 extends Thread {
	
	private ServerSocket ss;
	private List<Socket> connections = new ArrayList<Socket>();
	
	public static void main(String[] args) {
		new EchoTCP1().run();
	}
	
	public EchoTCP1() {
		try {
			ss = new ServerSocket(1338);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {
		while (true) {
			try {
				Socket s = ss.accept();
				connections.add(s);
				
				InetAddress address = s.getInetAddress();
				System.out.println("Name: " + address.getHostName());
				
				InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream();
				
				byte[] buffer = new byte[512];
				
				while (is.read(buffer) != -1) {
					os.write(buffer);
					os.flush();
				}
				
				is.close();
				os.close();
				s.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
