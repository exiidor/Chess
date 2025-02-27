package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;

public abstract class Player {
	protected final PieceColor pieceColor;

	Player(PieceColor pieceColor) {
		this.pieceColor = pieceColor;
	}

	public abstract Move chooseMove(Board board);

	public PieceColor getPieceColor() {
		return pieceColor;
	}

	public PieceColor getOpponentPieceColor() {
		return pieceColor.getOpposite();
	}
}
