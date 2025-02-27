package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;

public class HumanPlayer extends Player {
	private PieceColor pieceColor;

	public HumanPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		return null;
	}
}
