package gui.multilabel;

import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.MessageLabel;

public class UserListArea extends MultiLabel {

	private static final long serialVersionUID = 1L;
	
	private static final String USER_LIST_HEADER = "Userlist:";
	private static final String INDENTATION = "    ";
	
	private JTextField inputTextField;

	public UserListArea(JTextField inputTextField) {
		super();
		super.addLine(USER_LIST_HEADER);
		this.inputTextField = inputTextField;
	}
	
	@Override
	protected JLabel getLabel() {
		return new MessageLabel(inputTextField);
	}
	
	@Override
	public void addLine(Object line) {
		super.addLine(INDENTATION + line);
	}
	
	// Ta inte bort raden som säger "Userlist:"
	@Override
	public void removeLine(Object line) {
		if (extendedList.size() > 1) {
			super.removeLine(INDENTATION + line);
		}
	}
}
