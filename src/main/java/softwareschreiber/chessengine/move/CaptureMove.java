package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Piece;

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
	public Move copyWith(Board board) {
		return new CaptureMove(getSourcePos(), getTargetPos(), board.getPieceAt(other.getPosition()));
	}
}
