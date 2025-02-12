package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.move.EnPassantMove;
import softwareschreiber.chessengine.move.Move;

class EnPassantTest {
	@Test
	void test() {
		onlyTwoPieces(new Board(new CliGame()), true);
	}

	private void onlyTwoPieces(Board board, boolean isWhite) {
		Pawn pawn = board.addPiece(1, 1, new Pawn(isWhite, board));
		Pawn enemyPawn = board.addPiece(2, 3, new Pawn(!isWhite, board));
		board.move(pawn, new Move(pawn.getX(), pawn.getY(), pawn.getX(), pawn.getY() + pawn.getDirection() * 2), false);

		assertEquals(pawn.getY(), enemyPawn.getY());
		assertEquals(pawn.getX(), enemyPawn.getX() + (isWhite ? -1 : 1));

		Set<? extends Move> enemyMoves = enemyPawn.getValidMovesInternal();
		boolean hasEnPassant = false;

		for (Move move : enemyMoves) {
			if (move instanceof EnPassantMove enPassantMove) {
				assertTrue(enPassantMove.getCaptured().equals(pawn));
				hasEnPassant = true;
			}
		}

		assertTrue(hasEnPassant, enemyMoves.toString());
	}
}
