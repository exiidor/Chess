package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chessengine.gamepieces.PieceColor.WHITE;

import java.util.Set;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.EnPassantMove;
import softwareschreiber.chessengine.move.Move;

class EnPassantTest {
	@Test
	void test() {
		onlyTwoPieces(new Board(new CliGame(WHITE)), WHITE);
	}

	private void onlyTwoPieces(Board board, PieceColor color) {
		TestPlayer player = new TestPlayer(color);
		Pawn pawn = board.addPiece(1, 1, new Pawn(color, board));
		Pawn enemyPawn = board.addPiece(2, 3, new Pawn(color.getOpposite(), board));

		board.move(pawn, new Move(pawn.getX(), pawn.getY(), pawn.getX(), pawn.getY() + pawn.getDirection() * 2), player);

		assertEquals(pawn.getY(), enemyPawn.getY());
		assertEquals(pawn.getX(), enemyPawn.getX() + (color == WHITE ? -1 : 1));

		Set<? extends Move> enemyMoves = enemyPawn.getSafeMoves();
		boolean hasEnPassant = false;

		for (Move move : enemyMoves) {
			if (move instanceof EnPassantMove enPassantMove) {
				assertEquals(enPassantMove.getCaptured(), pawn);
				hasEnPassant = true;
			}
		}

		assertTrue(hasEnPassant, enemyMoves.toString());
	}
}
