package gui.multilabel;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.MessageLabel;

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
