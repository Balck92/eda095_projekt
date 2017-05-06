package gui;

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

	private List<String> messageList = new ArrayList<String>();
	private List<JLabel> labelList = new ArrayList<JLabel>();	// Listan av text.
	
	public MultiLabel() {
		super();
		setLayout(new GridLayout(0, 1));
	}
	
	protected JLabel getLabel() {
		return new JLabel();
	}
	
	// L�gg till en label.
	public Component add(JLabel label) {
		super.add(label);
		labelList.add(label);
		return label;
	}
	
	// L�gg till ett meddelande l�ngst ner.
	public void addLine(String line) {
		messageList.add(line);
		resize();
	}
	
	@Override
	public Component add(Component c) {	// Ska inte kunna l�gga till n�got �n JLabel.
		System.exit(1);
		return null;
	}
	
	public void resize() {
		while (messageList.size() >= 100) {
			messageList.remove(0);
		}
		
		int maxSize = (int) (START_LINES / 350.0 * getHeight());
		maxSize = Math.min(maxSize, MAX_LINES);	// Inte fler meddelanden �n MAX_LINES.
		if (labelList.size() != maxSize) {
			super.removeAll();	// Ta bort alla labels.
			while (labelList.size() < maxSize) {
				//labelList.add(new MessageLabel(inputTextField));
				labelList.add(getLabel());
			}
			while (labelList.size() > maxSize) {
				labelList.remove(labelList.size() - 1);
			}
			for (JLabel label : labelList) {	// L�gg till r�tt antal.
				super.add(label);
			}
		}
		
		for (int i = 0; i < labelList.size(); i++) {	// Skriv meddelandena i labels.
			int index = messageList.size() - labelList.size() + i;
			if (index > 0) {
				labelList.get(i).setText(messageList.get(index));
			}
		}
	}
}
