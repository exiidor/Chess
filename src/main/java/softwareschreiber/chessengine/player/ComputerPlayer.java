package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.evaluation.Evaluation;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.move.Move;

public class ComputerPlayer extends Player {
	public ComputerPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return new Evaluation(board.getGame(), board).bestMove(3, pieceColor);
	}

	@Override
	public Piece getPromotionTarget(Board board, Pawn pawn) {
		return new Queen(pieceColor, board);
	}
}
