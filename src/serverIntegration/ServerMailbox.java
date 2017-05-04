package serverIntegration;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

public class ServerMailbox {

	private Vector<Writer> outputs = new Vector<Writer>();
	
	public void addWriter(Writer writer) {
		outputs.addElement(writer);
	}

	public synchronized void broadcast(String message) {
		for (Writer writer : outputs) {
			try {
				String httpResponse = "HTTP/1.1 200 OK \r\n\r\n" + message + "\r\n";
				writer.write(httpResponse);
				writer.flush();
			} catch (IOException e) {
				outputs.remove(writer);
				e.printStackTrace();
			}
		}
	}
	
	public void removeWriter(Writer writer) {
		outputs.remove(writer);
	}
}
