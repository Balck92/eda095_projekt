package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

// Läser meddelande och skriver ut dem till log-filen.
public class ServerLogReader extends BufferedReader {
	
	private static final String SERVER_LOG_FILE_PATH = "log_server";
	
	private Writer logWriter;
	
	public ServerLogReader(Reader reader) {
		super(reader);
		try {
			logWriter = new BufferedWriter(new FileWriter(SERVER_LOG_FILE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public String readLine() throws IOException {
		String line = super.readLine();
		logWriter.write(line + "\r\n");
		logWriter.flush();
		return line;
	}

	@Override
	public void close() throws IOException {
		super.close();
		logWriter.close();
	}
}
