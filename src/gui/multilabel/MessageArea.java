package gui.multilabel;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;

import gui.MessageLabel;

public class MessageArea extends MultiLabel {

	private static final long serialVersionUID = 1L;
	private static final int ROWS_PER_IMAGE = 10;
	
	private JTextField inputTextField;
	
	public MessageArea(JTextField inputTextField) {
		super();
		this.inputTextField = inputTextField;
	}

	@Override
	protected JLabel getLabel() {
		return new MessageLabel(inputTextField);
	}
	
	public void addImage(BufferedImage bImage) {
		JLabel label = labelList.getLast();
		double scaleAmount = Math.min(label.getWidth() / ((double) bImage.getWidth()),	// Hur mycket bilden ska skalas ner för att få plats.
				ROWS_PER_IMAGE * label.getHeight() / ((double) bImage.getHeight()));
		int scaledHeight = (int) (scaleAmount * bImage.getHeight());
		int scaledWidth = (int) (scaleAmount * bImage.getWidth());
		
		int dy = bImage.getHeight() / ROWS_PER_IMAGE;
		for (int i = 0; i < ROWS_PER_IMAGE; i++) {
			BufferedImage subImage = bImage.getSubimage(0, dy * i, bImage.getWidth(), dy);
			int subImageHeight = scaledHeight / ROWS_PER_IMAGE;
			Image image = subImage.getScaledInstance(scaledWidth, subImageHeight, Image.SCALE_DEFAULT);
			ImageIcon imageIcon = new ImageIcon(image);
			addLine(imageIcon);
		}
	}
}
