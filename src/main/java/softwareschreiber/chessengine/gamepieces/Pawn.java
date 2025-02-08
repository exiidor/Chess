package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;

public class Pawn extends Piece {
	public Pawn(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public char getSymbol() {
		return 'P';
	}

	private int getDirection() {
		return isWhite ? 1 : -1;
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<Move> moves = new LinkedHashSet<>();
		boolean canMoveTwo;

		if (isWhite) {
			canMoveTwo = getY() == 1;
		} else {
			canMoveTwo = getY() == 6;
		}

		Position forwardPos = new Position(getX(), getY() + getDirection());
		Position forwardByTwoPos = new Position(getX(), getY() + getDirection() * 2);
		Move forwardMove = new Move(getPosition(), forwardPos);
		Move forwardByTwoMove = new Move(getPosition(), forwardByTwoPos);


		if (board.getPieceAt(forwardPos) == null) {
			moves.add(forwardMove);

			if (canMoveTwo && board.getPieceAt(forwardByTwoPos) == null) {
				moves.add(forwardByTwoMove);
			}
		}

		Position forwardLeftPos = new Position(getX() - 1, getY() + getDirection());
		Position forwardRightPos = new Position(getX() + 1, getY() + getDirection());
		Move forwardLeftMove = new Move(getPosition(), forwardLeftPos);
		Move forwardRightMove = new Move(getPosition(), forwardRightPos);
		Piece forwardLeftPiece = board.getPieceAt(forwardLeftPos);
		Piece forwardRightPiece = board.getPieceAt(forwardRightPos);


		if (forwardLeftPiece != null && forwardLeftPiece.isEnemyOf(this)) {
			moves.add(forwardLeftMove);
		}

		if (forwardRightPiece != null && forwardRightPiece.isEnemyOf(this)) {
			moves.add(forwardRightMove);
		}

		return moves;
	}
}

/*
* TODO: En passant & Promotion
* En-passant : Check for recently two Square push of enemy pawns, check for
* position of own pawn and the enemy pawn, boolean hasMovedTwo
* Promotion : Change the class for pawns in last row -> Queen, Bishop, Kinght
* or Rook
*/
