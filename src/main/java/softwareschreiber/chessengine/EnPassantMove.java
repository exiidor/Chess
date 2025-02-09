package softwareschreiber.chessengine;

import softwareschreiber.chessengine.gamepieces.Piece;

public class EnPassantMove extends Move {
	private final Piece other;

	public EnPassantMove(Position position, Position targetPosition, Piece captured) {
		super(position, targetPosition);
		this.other = captured;
	}

	public Piece getCaptured() {
		return other;
	}
}
