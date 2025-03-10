package softwareschreiber.chessengine.gui;

import javax.swing.JOptionPane;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Game;
import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;

public class GuiGame extends Game {
	public GuiGame(PieceColor startingColor) {
		super(startingColor);
	}

	@Override
	public Piece getPromotionTarget(Board board, Pawn pawn) {
		String[] options = { "Queen", "Rook", "Bishop", "Knight" };
		int choice = JOptionPane.showOptionDialog(
				null,
				"Choose piece to promote to:",
				"Pawn Promotion",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);

		return switch (choice) {
			case 0 -> new Queen(pawn.getColor(), board);
			case 1 -> new Rook(pawn.getColor(), board);
			case 2 -> new Bishop(pawn.getColor(), board);
			case 3 -> new Knight(pawn.getColor(), board);
			default -> getPromotionTarget(board, pawn);
		};
	}

	@Override
	public void checkMate(PieceColor winningColor) {
		JOptionPane.showMessageDialog(
				null,
				winningColor.getOpposite() + " is in checkmate. Game over.",
				"Checkmate",
				JOptionPane.INFORMATION_MESSAGE);
		endGame();
	}

	@Override
	public void staleMate() {
		JOptionPane.showMessageDialog(
				null,
				"Stalemate. Game over.",
				"Stalemate",
				JOptionPane.INFORMATION_MESSAGE);
		endGame();
	}
}
