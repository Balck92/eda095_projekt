package gui.multilabel;

public class UserListArea extends MultiLabel {

	private static final long serialVersionUID = 1L;
	
	private static final String USER_LIST_HEADER = "Userlist:";
	private static final String INDENTATION = "    ";

	public UserListArea() {
		super();
		super.addLine(USER_LIST_HEADER);
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
