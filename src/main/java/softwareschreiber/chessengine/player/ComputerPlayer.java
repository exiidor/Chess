package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.evaluation.Evaluation;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;

public class ComputerPlayer extends Player {
	public ComputerPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return new Evaluation(board).bestMove(4, pieceColor);
	}
}
