package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Piece;

public class PromotionMove extends CaptureMove {
	private Piece replacement;

	public PromotionMove(Position position, Position targetPosition, Piece captured, Piece replacement) {
		super(position, targetPosition, captured);
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
	public PromotionMove copyWith(Board board) {
		Piece capturedCopy = getCaptured() == null ? null : board.getPieceAt(getCaptured().getPosition());

		return new PromotionMove(getSourcePos(), getTargetPos(), capturedCopy, replacement.copyWith(board));
	}
}
