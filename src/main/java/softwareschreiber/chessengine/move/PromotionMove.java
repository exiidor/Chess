package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Board;
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

	@Override
	public String toString() {
		return super.toString() + " Promotion to " + replacement;
	}

	@Override
	public Move copyWith(Board board) {
		PromotionMove copy = new PromotionMove(getSourcePos(), getTargetPos(), board.getPieceAt(getCaptured().getPosition()));

		if (replacement != null) {
			copy.setReplacement(board.getPieceAt(replacement.getPosition()));
		}

		return copy;
	}
}
