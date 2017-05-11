package gui.multilabel;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
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
	
	public void addImage(BufferedImage bImage) {
		JLabel label = labelList.getLast();

		Image image = bImage.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT);
		ImageIcon imageIcon = new ImageIcon(image);
		addLine(imageIcon);
	}
}
