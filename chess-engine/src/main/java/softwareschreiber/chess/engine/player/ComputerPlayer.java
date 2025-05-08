package softwareschreiber.chess.engine.player;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.evaluation.Evaluation;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

public class ComputerPlayer extends Player {
	public ComputerPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		int pieceCount = board.getPieces().size();
		int level;

		if (pieceCount < 6) {
			level = 7;
		} else if (pieceCount < 8) {
			level = 6;
		} else if (pieceCount < 10) {
			level = 5;
		} else if (pieceCount < 18) {
			level = 4;
		} else {
			level = 3;
		}

		return new Evaluation(board.getGame(), board).bestMove(level, pieceColor);
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> move) {
		throw new IllegalStateException();
	}
}
