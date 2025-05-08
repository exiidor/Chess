package softwareschreiber.chess.engine;

import java.util.Set;

import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.PromotionMove;

public class TestGame extends Game {
	public TestGame(PieceColor color) {
		super(color);
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void checkMate(PieceColor winningColor) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void staleMate() {
		throw new UnsupportedOperationException();
	}
}
