package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Piece;

public class EnPassantMove extends CaptureMove {
	public EnPassantMove(Position position, Position targetPosition, Piece captured) {
		super(position, targetPosition, captured);
	}
}
