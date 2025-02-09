package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Piece;

public class PromotionMove extends CaptureMove {
	private Piece replacement;

	public PromotionMove(Position position, Position targetPosition, Piece captured) {
		super(position, targetPosition, captured);
	}

	public void setReplacement(Piece replacement) {
		this.replacement = replacement;
	}

	public Piece getReplacement() {
		return replacement;
	}
}
