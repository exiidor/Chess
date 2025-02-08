package softwareschreiber.chessengine;

public class CastlingMove extends Move {
	private Piece other;
	private Move otherMove;

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
