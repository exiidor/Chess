package softwareschreiber.chess.engine;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chess.engine.gamepieces.PieceColor.BLACK;
import static softwareschreiber.chess.engine.gamepieces.PieceColor.WHITE;

import org.junit.jupiter.api.Test;

import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.gamepieces.Queen;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.Player;

class PromotionTest {
	@Test
	void test() {
		onlyOnePiece(new Board(new TestGame()), WHITE);
		onlyOnePiece(new Board(new TestGame()), BLACK);
		capturePromoting(new Board(new TestGame()), WHITE);
		capturePromoting(new Board(new TestGame()), BLACK);
	}

	private void onlyOnePiece(Board board, PieceColor color) {
		boolean isWhite = color == WHITE;
		Position srcPos = new Position(1, isWhite ? 6 : 1);
		Position destPos = new Position(1, isWhite ? 7 : 0);

		Player player = new TestPlayer(color);
		Pawn pawn = board.addPiece(srcPos, new Pawn(color, board));
		board.move(pawn, new PromotionMove(srcPos, destPos, null, new Queen(color, board)), player);

		assertInstanceOf(Queen.class, board.getPieceAt(destPos));
	}

	private void capturePromoting(Board board, PieceColor color) {
		boolean isWhite = color == WHITE;
		Position srcPos = new Position(1, isWhite ? 6 : 1);
		Position destPos = new Position(1, isWhite ? 7 : 0);

		Player player = new TestPlayer(color);
		Pawn pawn = board.addPiece(srcPos, new Pawn(color, board));
		Piece enemy = board.addPiece(destPos, new Pawn(color.getOpposite(), board));
		board.move(pawn, new PromotionMove(srcPos, destPos, enemy, new Queen(color, board)), player);

		assertTrue(board.getEnemyPieces(pawn).isEmpty());
		assertInstanceOf(Queen.class, board.getPieceAt(destPos));
	}
}
