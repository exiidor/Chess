package softwareschreiber.chessengine.player;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.move.Move;

public abstract class Player {
	public abstract Move chooseMove(Board board, Piece piece);
}
