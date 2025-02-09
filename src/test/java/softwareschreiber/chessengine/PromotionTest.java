package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Queen;

class PromotionTest {
	//@Test
	void test() {
		onlyOnePiece(new Board(), true);
		onlyOnePiece(new Board(), false);
	}

	public void onlyOnePiece(Board board, boolean isWhite) {
		Pawn pawn = board.addPiece(1, isWhite ? 6 : 1, new Pawn(isWhite, board));
		board.move(pawn, new Move(pawn.getX(), pawn.getY(), pawn.getX(), pawn.getY() + pawn.getDirection()));

		assertEquals(board.getPieceAt(1, isWhite ? 7 : 0) instanceof Queen, true);
	}
}
