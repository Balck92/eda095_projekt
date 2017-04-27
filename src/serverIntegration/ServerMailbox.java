package serverIntegration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

public class ServerMailbox {

	private Vector<BufferedWriter> outputs = new Vector<BufferedWriter>();
	
	public void addWriter(BufferedWriter writer) {
		outputs.addElement(writer);
	}

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
	
	public void removeWriter(BufferedWriter writer) {
		outputs.remove(writer);
	}
}
