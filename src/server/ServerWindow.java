package server;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ServerWindow {
	
	private static final String PORT_LABEL_TEXT = "Running on port %d.";
	private static final String ONLINE_LABEL_TEXT = "Server has been online for %s.";
	private static final String LAST_MESSAGE_LABEL_TEXT = "Last message received was \"%s\" %s ago.";
	private static final String SERVER_LOG_FILE_PATH = "log_server.txt";
	
	private static Writer logWriter;
	static {
		try {
			logWriter = new BufferedWriter(new FileWriter(SERVER_LOG_FILE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private JFrame frame = new JFrame();
	private JButton quitButton = new JButton("Quit");
	private JLabel portLabel;
	private JLabel timeOnlineLabel = new JLabel(String.format(ONLINE_LABEL_TEXT, 0, 0));
	private JLabel lastMessageLabel = new JLabel("No message received.");
	private ServerWindowThread thread = new ServerWindowThread();
	
	private long timeLastMessage;
	private String lastMessage;

	public ServerWindow(int width, int height, int port) {
		frame.setPreferredSize(new Dimension(width, height));
		portLabel = new JLabel(String.format(PORT_LABEL_TEXT, port));
	}

	public void start() {
		frame.setTitle("Chat Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setLayout(new GridLayout(0, 1));
		frame.add(portLabel);
		frame.add(timeOnlineLabel);
		frame.add(lastMessageLabel);
		frame.add(quitButton);
		
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		thread.start();
	}
	
	public void setLastMessage(String message) {
		timeLastMessage = System.currentTimeMillis();
		lastMessage = message;
		logLine(message);
	}
	
	private void logLine(String line) {
		try {
			logWriter.write(line + "\r\n");
			logWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String minSecSince(long time) {
		long sec = (System.currentTimeMillis() - time) / 1000L;
		return String.format("%d min %d sec", sec / 60L, sec % 60);
	}

	private class ServerWindowThread extends Thread {
		
		long startTime;

		public ServerWindowThread() {
			startTime = System.currentTimeMillis();
			setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			while (true) {
				timeOnlineLabel.setText(String.format(ONLINE_LABEL_TEXT, minSecSince(startTime)));
				if (lastMessage != null) {
					lastMessageLabel.setText(String.format(LAST_MESSAGE_LABEL_TEXT, lastMessage, minSecSince(timeLastMessage)));
				}
			}
		}
		
	}
	
}
