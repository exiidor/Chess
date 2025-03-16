package softwareschreiber.chessengine;

import java.util.Set;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.player.Player;

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
