package softwareschreiber.chess.engine.player;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

public class HumanPlayer extends Player {
	private final Game game;

	public HumanPlayer(PieceColor pieceColor, Game game) {
		super(pieceColor);
		this.game = game;
	}

	@Override
	public Move chooseMove(Board board) {
		return null;
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		return game.choosePromotionMove(board, pawn, moves);
	}
}
