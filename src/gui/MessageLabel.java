package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class MessageLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	private JTextField inputText;
	
	public MessageLabel(JTextField inputText) {
		super("-");
		this.inputText = inputText;
		addMouseListener(new MessageMouseListener());
	}
	
	private class MessageMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			String text = getText();
			if (text.contains(":")) {
				String name = text.substring(0, text.indexOf(':'));
				if (name.charAt(0) == '[') {
					name = name.substring(1, name.length() - 1);
				}
				inputText.setText(name + ":" + inputText.getText());
			}
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
