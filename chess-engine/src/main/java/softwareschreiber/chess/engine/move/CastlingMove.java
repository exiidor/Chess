package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Piece;

public class CastlingMove extends AbstractMove {
	private final Piece other;
	private final Move otherMove;

	public CastlingMove(Position position, Position targetPosition, Piece other, Position otherTargetPosition) {
		super(position, targetPosition);
		this.other = other;
		this.otherMove = new NormalMove(other.getPosition(), otherTargetPosition);
	}

	public Piece getOther() {
		return other;
	}

	public Move getOtherMove() {
		return otherMove;
	}

	@Override
	public String toString() {
		return super.toString() + " Castling with " + other;
	}

	@Override
	public CastlingMove copyWith(Board board) {
		return new CastlingMove(getSourcePos(), getTargetPos(), board.getPieceAt(other.getPosition()), otherMove.getTargetPos());
	}
}
