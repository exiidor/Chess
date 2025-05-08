package softwareschreiber.chess.engine;

import java.util.Set;

import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.gamepieces.Queen;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.Player;

public class TestPlayer extends Player {
	public TestPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return null;
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		for (PromotionMove promotionMove : moves) {
			if (promotionMove.getReplacement() instanceof Queen) {
				return promotionMove;
			}
		}

		throw new IllegalStateException();
	}
}
