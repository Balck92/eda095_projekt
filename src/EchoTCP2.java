import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoTCP2 extends Thread {

	
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(1338);
			while (true) {
				Socket s = ss.accept();
				new EchoTCP2(s).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private Socket s;
	
	public EchoTCP2(Socket s) {
		this.s = s;
	}
	
	public void run() {
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
