package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Piece;

public class CaptureMove extends Move {
	private final Piece other;

	public CaptureMove(Position position, Position targetPosition, Piece captured) {
		super(position, targetPosition);
		this.other = captured;
	}

	public Piece getCaptured() {
		return other;
	}

	@Override
	public String toString() {
		return super.toString() + " Capture " + other;
	}

	@Override
	public CaptureMove copyWith(Board board) {
		return new CaptureMove(getSourcePos(), getTargetPos(), board.getPieceAt(other.getPosition()));
	}
}
