package softwareschreiber.chessengine;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.player.Player;

public class TestPlayer extends Player {
	public TestPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return null;
	}

	@Override
	public Piece getPromotionTarget(Board board, Pawn pawn) {
		return new Queen(pawn.getColor(), board);
	}
}
