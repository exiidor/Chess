package softwareschreiber.chess.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chess.engine.gamepieces.PieceColor.BLACK;
import static softwareschreiber.chess.engine.gamepieces.PieceColor.WHITE;

import java.util.Set;

import org.junit.jupiter.api.Test;

import softwareschreiber.chess.engine.gamepieces.King;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.gamepieces.Rook;
import softwareschreiber.chess.engine.move.CastlingMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.player.Player;

class CastlingTest {
	@Test
	void test() {
		noObstruction(new Board(new TestGame(WHITE)), WHITE, true);
		noObstruction(new Board(new TestGame(WHITE)), WHITE, false);
		noObstruction(new Board(new TestGame(WHITE)), BLACK, true);
		noObstruction(new Board(new TestGame(WHITE)), BLACK, false);

		obstruction(new Board(new TestGame(WHITE)), WHITE, true);
		obstruction(new Board(new TestGame(WHITE)), WHITE, false);
		obstruction(new Board(new TestGame(WHITE)), BLACK, true);
		obstruction(new Board(new TestGame(WHITE)), BLACK, false);
	}

	private void noObstruction(Board board, PieceColor color, boolean left) {
		int y = color == WHITE ? 7 : 0;

		King king = board.addPiece(4, y, new King(color, board));
		Rook rook = board.addPiece(left ? 0 : 7, y, new Rook(color, board));

		Player player = new TestPlayer(color);
		Set<? extends Move> kingMoves = king.getSafeMoves();
		int castleCount = 0;

		for (Move move : kingMoves) {
			if (move instanceof CastlingMove castlingMove) {
				assertEquals(0, castleCount);

				board.move(king, castlingMove, player);

				assertTrue(king.getPosition().equals(new Position(left ? 2 : 6, king.getY())));
				assertTrue(rook.getPosition().equals(new Position(left ? 3 : 5, y)));

				castleCount++;
			}
		}
	}

	private void obstruction(Board board, PieceColor color, boolean left) {
		int y = color == WHITE ? 7 : 0;

		King king = board.addPiece(4, y, new King(color, board));
		board.addPiece(3, 7 - y, new King(color.getOpposite(), board));
		board.addPiece(left ? 0 : 7, y, new Rook(color, board));
		board.addPiece(left ? 3 : 5, 4, new Rook(color.getOpposite(), board));

		Set<? extends Move> kingMoves = king.getValidMoves();
		int castleCount = 0;

		for (Move move : kingMoves) {
			if (move instanceof CastlingMove) {
				castleCount++;
			}
		}

		assertEquals(0, castleCount);
	}
}
