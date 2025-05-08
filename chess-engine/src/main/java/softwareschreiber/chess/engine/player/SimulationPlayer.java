package softwareschreiber.chess.engine.player;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.evaluation.Evaluation;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

public class SimulationPlayer extends Player {
	public SimulationPlayer(PieceColor pieceColor) {
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
