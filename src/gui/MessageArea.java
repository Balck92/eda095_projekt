package gui;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class MessageArea extends MultiLabel {

	private static final long serialVersionUID = 1L;
	
	private JTextField inputTextField;
	
	public MessageArea(JTextField inputTextField) {
		super();
		this.inputTextField = inputTextField;
	}

	@Override
	protected JLabel getLabel() {
		return new MessageLabel(inputTextField);
	}
}
