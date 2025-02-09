package softwareschreiber.chessengine.move;

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
}
