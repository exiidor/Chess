package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.Rook;

class CastlingTest {
	@Test
	void test() {
		noObstruction(new Board(), true, true);
		noObstruction(new Board(), true, false);
		noObstruction(new Board(), false, true);
		noObstruction(new Board(), false, false);

		obstruction(new Board(), true, true);
		obstruction(new Board(), true, false);
		obstruction(new Board(), false, true);
		obstruction(new Board(), false, false);
	}

	private void noObstruction(Board board, boolean isWhite, boolean left) {
		int y = isWhite ? 7 : 0;

		King king = board.addPiece(4, y, new King(isWhite, board));
		Rook rook = board.addPiece(left ? 0 : 7, y, new Rook(isWhite, board));

		Set<? extends Move> kingMoves = king.getValidMoves();
		int castleCount = 0;

		for (Move move : kingMoves) {
			if (move instanceof CastlingMove castlingMove) {
				assertTrue(castleCount == 0);

				board.move(king, castlingMove);

				assertTrue(king.getPosition().equals(new Position(left ? 2 : 6, king.getY())));
				assertTrue(rook.getPosition().equals(new Position(left ? 3 : 5, y)));

				castleCount++;
			}
		}
	}

	private void obstruction(Board board, boolean isWhite, boolean left) {
		int y = isWhite ? 7 : 0;

		King king = board.addPiece(4, y, new King(isWhite, board));
		board.addPiece(left ? 0 : 7, y, new Rook(isWhite, board));
		board.addPiece(left ? 3 : 5, 4, new Rook(!isWhite, board));

		Set<? extends Move> kingMoves = king.getValidMoves();
		int castleCount = 0;

		for (Move move : kingMoves) {
			if (move instanceof CastlingMove) {
				castleCount++;
			}
		}

		assertTrue(castleCount == 0);
	}
}
