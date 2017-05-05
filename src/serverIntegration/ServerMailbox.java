package serverIntegration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

// Skickar meddelande till klienterna.
public class ServerMailbox {

	private Vector<BufferedWriter> outputs = new Vector<BufferedWriter>();

	public void addWriter(BufferedWriter writer) {
		outputs.addElement(writer);
	}

	// Skickar till alla klienter.
	public synchronized void broadcast(String message) {
		for (BufferedWriter out : outputs) {
			try {
				out.write(message + "\r\n");
				out.flush();
			} catch (IOException e) {
				outputs.remove(out);
				e.printStackTrace();
			}
		}
	}

	// Skickar bara till en.
	public static void sendMessage(String message, BufferedWriter bw) {
		try {
			bw.write(message + "\r\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void removeWriter(BufferedWriter writer) {
		outputs.remove(writer);
	}
}
