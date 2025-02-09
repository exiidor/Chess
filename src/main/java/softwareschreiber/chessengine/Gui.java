package softwareschreiber.chessengine;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import softwareschreiber.chessengine.util.Util;

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
					char firstLetterOfFileName = (piece.getName() + piece.isWhite).toUpperCase().charAt(0);
					String restOfFileName = (piece.getName() + piece.isWhite).substring(1).toLowerCase();
					String nameOfFile = (firstLetterOfFileName + restOfFileName).trim();
					JLabel picture = new JLabel(new ImageIcon(Util.getResource("/graphics/" + nameOfFile + ".png").toString()));
					System.out.println(firstLetterOfFileName + restOfFileName);
					square.add(picture);
				}

				square.setBackground(white ? Color.WHITE : new Color(36, 36, 36));
				panel.add(square);
				white = !white;
			}

			white = !white;
		}

		frame.add(panel);

		frame.setSize(1024, 1024);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Gui();
	}
}
