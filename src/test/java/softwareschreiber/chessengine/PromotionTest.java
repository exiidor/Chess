package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.Queen;

class PromotionTest {
	@Test
	void test() {
		onlyOnePiece(createBoard(), true);
		onlyOnePiece(createBoard(), false);
	}

	private void onlyOnePiece(Board board, boolean isWhite) {
		Pawn pawn = board.addPiece(1, isWhite ? 6 : 1, new Pawn(isWhite, board));
		board.move(pawn, new Move(pawn.getX(), pawn.getY(), pawn.getX(), pawn.getY() + pawn.getDirection()));

		assertEquals(board.getPieceAt(1, isWhite ? 7 : 0) instanceof Queen, true);
	}

	private Board createBoard() {
		return new Board() {
			@Override
			protected Piece promote(Pawn pawn) {
				return new Queen(pawn.isWhite(), this);
			}
		};
	}
}
