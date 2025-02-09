package softwareschreiber.chessengine.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChessPanel {
	private final JPanel panel;
	private final Color origColor;

	public ChessPanel(JPanel panel, Color origColor) {
		this.panel = panel;
		this.origColor = origColor;

		panel.setBackground(origColor);
	}

	public void resetColor() {
		panel.setBackground(origColor);
	}

	public void setColor(Color color) {
		panel.setBackground(color);
	}

	public void setPicture(ImageIcon icon) {
		panel.removeAll();
		panel.add(new JLabel(icon));
		panel.revalidate();
		panel.repaint();
	}

	public JPanel getUI() {
		return panel;
	}
}
