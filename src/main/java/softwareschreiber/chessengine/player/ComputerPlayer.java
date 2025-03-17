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
		int level = 3;
		if (board.getPieces().size() < 18) {
			level = 4;
		}
		if (board.getPieces().size() < 10) {
			level = 5;
		}
		if (board.getPieces().size() < 8) {
			level = 6;
		}
		if (board.getPieces().size() < 6) {
			level = 7;
		}
		return new Evaluation(board.getGame(), board).bestMove(level, pieceColor);
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> move) {
		throw new IllegalStateException();
	}
}
