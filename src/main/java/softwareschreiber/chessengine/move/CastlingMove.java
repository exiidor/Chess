package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Piece;

public class CastlingMove extends Move {
	private final Piece other;
	private final Move otherMove;

	public CastlingMove(Position position, Position targetPosition, Piece other, Position otherTargetPosition) {
		super(position, targetPosition);
		this.other = other;
		this.otherMove = new Move(other.getPosition(), otherTargetPosition);
	}

	public Piece getOther() {
		return other;
	}

	public Move getOtherMove() {
		return otherMove;
	}
}
