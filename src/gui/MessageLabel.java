package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import util.Communication;

public class MessageLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	private JTextField inputText;

	public MessageLabel(JTextField inputText) {
		super();
		this.inputText = inputText;
		addMouseListener(new MessageMouseListener());
	}

	private class MessageMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			String text = getText();
			String name = getName(text);
			whisperToName(name);
			inputText.requestFocus();
		}

		private String getName(String s) {
			if (s.contains("[")) {
				return s.substring(s.indexOf('[') + 1, s.indexOf(']'));
			}
			int nameStart, nameEnd;
			for (nameStart = 0; nameStart < s.length() && s.charAt(nameStart) == ' '; nameStart++) {
			} // Skippa alla space i början.
			for (nameEnd = nameStart; nameEnd < s.length() && s.charAt(nameEnd) != ' '; nameEnd++) {
			} // Hitta nästa space.
			return s.substring(nameStart, nameEnd);
		}

		private void whisperToName(String name) {
			if (name.isEmpty()) // Gör inget.
				return;

			String input = inputText.getText();

			if (input.startsWith(Communication.CHAT_PRIVATE_MESSAGE)) {
				input = input.substring(Communication.CHAT_PRIVATE_MESSAGE.length()); // Det
																						// efter
																						// "/w
																						// "
				int messageStart = input.indexOf(' '); // Meddelandet börjar
														// efter första space.
				if (messageStart == -1) // Inget meddelande
					return;
				input = input.substring(messageStart + 1); // Meddelandet.
			}
			setWhisper(name, input);
		}

		private void setWhisper(String name, String message) {
			inputText.setText(String.format("%s%s %s", Communication.CHAT_PRIVATE_MESSAGE, name, message));
		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}
	}
}
