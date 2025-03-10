package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;

public abstract class Player {
	protected final PieceColor pieceColor;

	protected Player(PieceColor pieceColor) {
		this.pieceColor = pieceColor;
	}

	public PieceColor getPieceColor() {
		return pieceColor;
	}

	public PieceColor getOpponentPieceColor() {
		return pieceColor.getOpposite();
	}

	public abstract Move chooseMove(Board board);

	public abstract Piece getPromotionTarget(Board board, Pawn pawn);
}
