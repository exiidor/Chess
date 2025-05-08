package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Piece;

public class EnPassantMove extends CaptureMove {
	public EnPassantMove(Position position, Position targetPosition, Piece captured) {
		super(position, targetPosition, captured);
	}

	@Override
	public String toString() {
		return super.toString() + " En passant";
	}
}
