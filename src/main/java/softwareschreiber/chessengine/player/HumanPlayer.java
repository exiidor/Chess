package softwareschreiber.chessengine.player;

import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Game;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;

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
