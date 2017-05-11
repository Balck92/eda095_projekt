package gui.multilabel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultiLabel extends JPanel {

	private static final long serialVersionUID = 1L;	// ?
	private static final int START_LINES = 20;
	private static final int MAX_LINES = START_LINES + START_LINES / 2;

	protected LinkedList<Object> extendedList = new LinkedList<Object>();
	protected LinkedList<JLabel> labelList = new LinkedList<JLabel>();	// Listan av text.
	
	public MultiLabel() {
		super();
		setLayout(new GridLayout(0, 1));
	}
	
	protected JLabel getLabel() {
		return new JLabel("");
	}
	
	// Lägg till ett meddelande längst ner.
	public void addLine(Object line) {
		if (extendedList.size() >= MAX_LINES) {	// Lagra inte för många meddelanden.
			extendedList.removeFirst();
		}
		extendedList.addLast(line);
		resize();
	}
	
	public void removeLine(Object line) {
		extendedList.remove(line);
		resize();
	}
	
	@Override
	public Component add(Component c) {	// Ska inte kunna lägga till något än JLabel.
		System.exit(1);
		return null;
	}
	
	public void resize() {
		int size = (int) (START_LINES / 350.0 * getHeight());	// Hur många JLabels vi ska ha i fönstret.
		size = Math.min(size, MAX_LINES);	// Inte fler meddelanden än MAX_LINES.
		
		if (labelList.size() != size) {	// Om vi måste ändra storlek.
			while (labelList.size() < size) {	// Om fönstret blev större.
				JLabel label = getLabel();		// Lägg till en ny label.
				labelList.addFirst(label);
				super.add(label, 0);
			}
			while (labelList.size() > size) {	// Om fönstret förminskades.
				labelList.removeFirst();
				super.remove(0);
			}
		}
		
		// Skriv meddelandena i labels.
		for (int i = 0; i < labelList.size(); i++) {
			int index = extendedList.size() - labelList.size() + i;
			if (index >= 0) {	// Om det finns ett meddelande att visa på raden.
				Object item = extendedList.get(index);
				System.out.println("resize(): " + item);
				if (item instanceof String) {
					labelList.get(i).setText((String) item);
				} else if (item instanceof ImageIcon) {
					labelList.get(i).setIcon((ImageIcon) item);
				}
			}
			else {			// Om det inte finns något meddelande på raden.
				labelList.get(i).setText("");
			}
		}
	}
}
