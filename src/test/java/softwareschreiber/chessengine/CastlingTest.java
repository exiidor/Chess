package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chessengine.gamepieces.PieceColor.BLACK;
import static softwareschreiber.chessengine.gamepieces.PieceColor.WHITE;

import java.util.Set;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Rook;
import softwareschreiber.chessengine.move.CastlingMove;
import softwareschreiber.chessengine.move.Move;

class CastlingTest {
	@Test
	void test() {
		noObstruction(new Board(new CliGame(WHITE)), WHITE, true);
		noObstruction(new Board(new CliGame(WHITE)), WHITE, false);
		noObstruction(new Board(new CliGame(WHITE)), BLACK, true);
		noObstruction(new Board(new CliGame(WHITE)), BLACK, false);

		obstruction(new Board(new CliGame(WHITE)), WHITE, true);
		obstruction(new Board(new CliGame(WHITE)), WHITE, false);
		obstruction(new Board(new CliGame(WHITE)), BLACK, true);
		obstruction(new Board(new CliGame(WHITE)), BLACK, false);
	}

	private void noObstruction(Board board, PieceColor color, boolean left) {
		int y = color == WHITE ? 7 : 0;

		King king = board.addPiece(4, y, new King(color, board));
		Rook rook = board.addPiece(left ? 0 : 7, y, new Rook(color, board));

		Set<? extends Move> kingMoves = king.getSafeMoves();
		int castleCount = 0;

		for (Move move : kingMoves) {
			if (move instanceof CastlingMove castlingMove) {
				assertTrue(castleCount == 0);

				board.move(king, castlingMove, false);

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

		assertTrue(castleCount == 0);
	}
}
