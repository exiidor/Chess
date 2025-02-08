package softwareschreiber.chessengine;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Gui {
	private Board board;

	public Gui() {
		JFrame frame = new JFrame("Software-Schreiber Schach");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		board = new Board();
		board.initializeStartingPositions();

		JPanel panel = new JPanel(new GridLayout(8, 8));
		boolean white = true;

		for (int y = board.getMinY(); y <= board.getMaxY(); y++) {
			for (int x = board.getMinX(); x <= board.getMaxX(); x++) {
				JPanel square = new JPanel();
				Piece piece = board.getPieceAt(x, y);

				if (piece != null) {
					JTextArea text = new JTextArea(piece.getName());
					text.setEditable(false);
					square.add(text);
				}

				square.setBackground(white ? Color.WHITE : Color.BLACK);
				panel.add(square);
				white = !white;
			}

			white = !white;
		}

		frame.add(panel);

		frame.setSize(800, 800);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Gui();
	}
}
