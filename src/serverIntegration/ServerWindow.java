package serverIntegration;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ServerWindow {
	
	private static final String PORT_LABEL_TEXT = "Running on port %d.";
	private static final String ONLINE_LABEL_TEXT = "Server has been online for %d minutes %d seconds.";

	private JFrame frame;
	private JButton quitButton;
	private JLabel portLabel;
	private JLabel onlineTimeLabel;
	private Thread thread;

	public ServerWindow(int width, int height, int port) {
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(width, height));
		quitButton = new JButton("Quit");
		portLabel = new JLabel(String.format(PORT_LABEL_TEXT, port));
		onlineTimeLabel = new JLabel(String.format(ONLINE_LABEL_TEXT, 0, 0));
		thread = new ServerWindowThread();
	}

	public void start() {
		frame.setTitle("ChatServer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(0, 1));
		frame.add(portLabel);
		frame.add(onlineTimeLabel);
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

	private class ServerWindowThread extends Thread {
		
		long startTime;

		public ServerWindowThread() {
			startTime = System.currentTimeMillis();
			setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			while (true) {
				long time = System.currentTimeMillis();
				long secondsSinceStart = (time - startTime) / 1000;
				onlineTimeLabel.setText(String.format(ONLINE_LABEL_TEXT, secondsSinceStart / 60, secondsSinceStart % 60));
			}
		}
	}
}