package softwareschreiber.chess.gui.manual;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.King;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.gui.Gui;

class PromotionTest {
	public static void main(String[] args) {
		Gui gui = new Gui();
		Game game = gui.getGame();
		Board board = game.getBoard();

		List<Piece> piecesToRemove = new ArrayList<>();

		for (Piece piece : board.getPieces()) {
			if (piece instanceof Pawn pawn && !pawn.isBlack()) {
				continue;
			}

			if (piece instanceof King) {
				continue;
			}

			piecesToRemove.add(piece);
		}

		for (Piece piece : piecesToRemove) {
			board.removePiece(piece);
		}

		SwingUtilities.invokeLater(() -> {
			gui.reloadSquares();
		});
	}
}
