package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ChatClient {

	public static void main(String[] args) {
		if (args.length < 2) {
			return;
		}
		String machine = args[0];
		String port = args[1];

		try {
			Socket s = new Socket(machine, Integer.parseInt(port));

			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
						String line;
						while ((line = reader.readLine()) != null && !line.isEmpty()) {	// while there is a message to send
							writer.write(line + "\r\n");
							writer.flush();
							if (Character.toUpperCase(line.charAt(0)) == 'Q') {	// Exit the client if the first character is Q.
								s.close();
								System.exit(0);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}.start();
			
			new Thread() {
				public void run() {
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
						String line;
						while (true) {
							line = reader.readLine();
							System.out.println(line);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
