package gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Fönster som frågar efter host och port.
// Måste anropa show() för att visa fönstret.
public class UserInputWindow {

	private String host;
	private int port;

	private JPanel panel;
	private JTextField hostField = new JTextField(40);
	private JTextField portField = new JTextField(40);

	public UserInputWindow() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JLabel("Host:"));
		panel.add(hostField);
		panel.add(Box.createVerticalStrut(15)); // a spacer
		panel.add(new JLabel("Port:"));
		panel.add(portField);
	}

	public void show(String promptText) {
		int result = JOptionPane.showConfirmDialog(null, panel, promptText, JOptionPane.OK_CANCEL_OPTION);
		hostField.requestFocus();
		if (result == JOptionPane.OK_OPTION) {
			host = hostField.getText();
			try {
				port = Integer.parseInt(portField.getText());	// Kör på användarens port.
			} catch (NumberFormatException e) {	// Skrev inget tal.
				port = 30000;					// Kör på 30000.
			}
		} else {
			System.exit(0);
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
