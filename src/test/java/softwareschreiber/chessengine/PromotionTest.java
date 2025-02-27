package softwareschreiber.chessengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static softwareschreiber.chessengine.gamepieces.PieceColor.BLACK;
import static softwareschreiber.chessengine.gamepieces.PieceColor.WHITE;

import org.junit.jupiter.api.Test;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.move.PromotionMove;

class PromotionTest {
	@Test
	void test() {
		onlyOnePiece(createBoard(), WHITE);
		onlyOnePiece(createBoard(), BLACK);
		capturePromoting(createBoard(), WHITE);
		capturePromoting(createBoard(), BLACK);
	}

	private void onlyOnePiece(Board board, PieceColor color) {
		boolean isWhite = color == WHITE;
		Position srcPos = new Position(1, isWhite ? 6 : 1);
		Position destPos = new Position(1, isWhite ? 7 : 0);

		Pawn pawn = board.addPiece(srcPos, new Pawn(color, board));
		board.move(pawn, new PromotionMove(srcPos, destPos, null), false);

		assertEquals(board.getPieceAt(destPos) instanceof Queen, true);
	}

	private void capturePromoting(Board board, PieceColor color) {
		boolean isWhite = color == WHITE;
		Position srcPos = new Position(1, isWhite ? 6 : 1);
		Position destPos = new Position(1, isWhite ? 7 : 0);

		Pawn pawn = board.addPiece(srcPos, new Pawn(color, board));
		Piece enemy = board.addPiece(destPos, new Pawn(color.getOpposite(), board));
		board.move(pawn, new PromotionMove(srcPos, destPos, enemy), false);

		assertTrue(board.getEnemyPieces(pawn).isEmpty());
		assertEquals(board.getPieceAt(destPos) instanceof Queen, true);
	}

	private Board createBoard() {
		return new Board(new CliGame(WHITE) {
			@Override
			protected Piece getPromotionTarget(Board board, Pawn pawn) {
				return new Queen(pawn.getColor(), board);
			}
		});
	}
}
