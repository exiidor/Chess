package softwareschreiber.chess.engine.player;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

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

	public abstract PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves);
}
