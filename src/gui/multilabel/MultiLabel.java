package gui.multilabel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultiLabel extends JPanel {

	private static final long serialVersionUID = 1L;	// ?
	private static final int START_LINES = 5;
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
	
	// L�gg till ett meddelande l�ngst ner.
	public void addLine(Object line) {
		if (extendedList.size() >= MAX_LINES) {	// Lagra inte f�r m�nga meddelanden.
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
	public Component add(Component c) {	// Ska inte kunna l�gga till n�got annat �n JLabel.
		System.exit(1);
		return null;
	}
	
	public void resize() {
		int size = (int) (START_LINES / 350.0 * getHeight());	// Hur m�nga JLabels vi ska ha i f�nstret.
		size = Math.min(size, MAX_LINES);	// Inte fler meddelanden �n MAX_LINES.
		
		if (labelList.size() != size) {	// Om vi m�ste �ndra storlek.
			while (labelList.size() < size) {	// Om f�nstret blev st�rre.
				JLabel label = getLabel();		// L�gg till en ny label.
				labelList.addFirst(label);
				super.add(label, 0);
			}
			while (labelList.size() > size) {	// Om f�nstret f�rminskades.
				labelList.removeFirst();
				super.remove(0);
			}
		}
		
		// Skriv meddelandena i labels.
		for (int i = 0; i < labelList.size(); i++) {
			int index = extendedList.size() - labelList.size() + i;
			if (index >= 0) {	// Om det finns ett meddelande att visa p� raden.
				Object item = extendedList.get(index);
				JLabel label = labelList.get(i);
				if (item instanceof String) {
					label.setIcon(null);
					label.setText((String) item);
				} else if (item instanceof ImageIcon) {
					label.setSize(label.getWidth() * 5, label.getHeight() * 5);
					label.setIcon((ImageIcon) item);
					label.setText("");
				}
			}
			else {			// Om det inte finns n�got meddelande p� raden.
				labelList.get(i).setText("");
			}
		}
	}
}
