package softwareschreiber.chess.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import softwareschreiber.chess.engine.gamepieces.Piece;

public class ChessPanel {
	private final JPanel panel;
	private final Color origColor;
	private Piece piece;

	public ChessPanel(JPanel panel, Color origColor) {
		this.panel = panel;
		this.origColor = origColor;

		panel.setBackground(origColor);
	}

	public void setColor(Color color) {
		panel.setBackground(color);
	}

	public void resetColor() {
		setColor(origColor);
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
		setPicture(piece == null ? null : Gui.getImageForPiece(piece));
	}

	public Piece getPiece() {
		return piece;
	}

	private void setPicture(ImageIcon icon) {
		panel.removeAll();
		panel.add(new JLabel(icon));
		panel.revalidate();
		panel.repaint();
	}

	public JPanel getUI() {
		return panel;
	}
}
