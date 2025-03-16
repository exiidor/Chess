package softwareschreiber.chessengine.player;

import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.evaluation.Evaluation;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;

public class ComputerPlayer extends Player {
	public ComputerPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return new Evaluation(board.getGame(), board).bestMove(3, pieceColor);
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> move) {
		throw new IllegalStateException();
	}
}
