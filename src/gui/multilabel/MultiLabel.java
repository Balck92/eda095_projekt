package gui.multilabel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MultiLabel extends JPanel {

	private static final long serialVersionUID = 1L;	// ?
	private static final int START_LINES = 20;
	private static final int MAX_LINES = START_LINES + START_LINES / 2;

	protected List<String> textList = new ArrayList<String>();
	protected List<JLabel> labelList = new ArrayList<JLabel>();	// Listan av text.
	
	public MultiLabel() {
		super();
		setLayout(new GridLayout(0, 1));
	}
	
	protected JLabel getLabel() {
		return new JLabel("-");
	}
	
	// L�gg till en label.
	public Component add(JLabel label) {
		super.add(label);
		labelList.add(label);
		return label;
	}
	
	// L�gg till ett meddelande l�ngst ner.
	public void addLine(String line) {
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
		while (textList.size() > MAX_LINES) {	// Lagra inte f�r m�nga meddelanden.
			textList.remove(0);
		}
		
		int maxSize = (int) (START_LINES / 350.0 * getHeight());
		maxSize = Math.min(maxSize, MAX_LINES);	// Inte fler meddelanden �n MAX_LINES.
		
		if (labelList.size() != maxSize) {	// Om vi m�ste �ndra storlek.
			super.removeAll();	// Ta bort alla labels.
			while (labelList.size() < maxSize) {
				labelList.add(getLabel());
			}
			while (labelList.size() > maxSize) {
				labelList.remove(labelList.size() - 1);
			}
			for (JLabel label : labelList) {	// L�gg till r�tt antal.
				if (label == null) {
					System.err.println("null i lista?");
				} else {
					super.add(label);
				}
			}
		}
		
		int index = textList.size() - labelList.size();
		for (int i = 0; i < labelList.size(); i++) {	// Skriv meddelandena i labels.
			if (index >= 0) {	// Finns meddelande att skriva p� raden.
				labelList.get(i).setText(textList.get(index));
			} else {			// Finns inget meddelande att skriva p� raden.
				labelList.get(i).setText("");
			}
			index++;
		}
	}
}
