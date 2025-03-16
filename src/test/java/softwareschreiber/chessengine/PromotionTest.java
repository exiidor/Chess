package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chessengine.gamepieces.PieceColor.BLACK;
import static softwareschreiber.chessengine.gamepieces.PieceColor.WHITE;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.player.Player;

class PromotionTest {
	@Test
	void test() {
		onlyOnePiece(new Board(new CliGame(WHITE)), WHITE);
		onlyOnePiece(new Board(new CliGame(WHITE)), BLACK);
		capturePromoting(new Board(new CliGame(WHITE)), WHITE);
		capturePromoting(new Board(new CliGame(WHITE)), BLACK);
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
