package softwareschreiber.chessengine.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Game;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.player.Player;

class PromotionTest {
	public static void main(String[] args) {
		Gui gui = new Gui();
		Game game = gui.getGame();
		Board board = game.getBoard();
		Player blackPlayer = game.getBlackPlayer();

		Pawn blackPawn = null;
		List<Piece> piecesToRemove = new ArrayList<>();

		for (Piece piece : board.getPieces()) {
			if (piece instanceof Pawn pawn && pawn.isBlack()) {
				blackPawn = pawn;
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

		board.move(blackPawn, new PromotionMove(blackPawn.getPosition(), new Position(blackPawn.getX(), board.getMinY()), null), blackPlayer);
	}
}
