package gui.multilabel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultiLabel extends JPanel {

	private static final long serialVersionUID = 1L;	// ?
	private static final int START_LINES = 20;
	private static final int MAX_LINES = START_LINES + START_LINES / 2;

	protected LinkedList<String> textList = new LinkedList<String>();
	protected LinkedList<JLabel> labelList = new LinkedList<JLabel>();	// Listan av text.
	
	public MultiLabel() {
		super();
		setLayout(new GridLayout(0, 1));
	}
	
	protected JLabel getLabel() {
		return new JLabel("");
	}
	
	// L�gg till en label.
	public Component add(JLabel label) {
		super.add(label);
		labelList.add(label);
		return label;
	}
	
	// L�gg till ett meddelande l�ngst ner.
	public void addLine(String line) {
		if (textList.size() >= MAX_LINES) {	// Lagra inte f�r m�nga meddelanden.
			textList.removeFirst();
		}
		textList.add(line);
		resize();
	}
	
	public void removeLine(String line) {
		textList.remove(line);
		resize();
	}
	
	@Override
	public Component add(Component c) {	// Ska inte kunna l�gga till n�got �n JLabel.
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
		for (int i = Math.max(0, labelList.size() - textList.size()); i < labelList.size(); i++) {
			int index = textList.size() - labelList.size() + i;
			labelList.get(i).setText(textList.get(index));
		}
	}
}
