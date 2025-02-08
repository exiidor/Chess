package softwareschreiber.chessengine.gamepieces;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;

public class Knight extends Piece {
	public Knight(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public char getSymbol() {
		return 'k';
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		Position forwardLeft = new Position(getX() - 1, getY() - 2);
		Position forwardRight = new Position(getX() + 1, getY() - 2);
		Position leftForward = new Position(getX() - 2, getY() - 1);
		Position leftBackward = new Position(getX() - 2, getY() + 1);
		Position rightForward = new Position(getX() + 2, getY() - 1);
		Position rightBackward = new Position(getX() + 2, getY() + 1);
		Position backwardLeft = new Position(getX() - 1, getY() + 2);
		Position backwardRight = new Position(getX() + 1, getY() + 2);

		List<Position> targetPositions = Arrays.asList(forwardLeft, forwardRight, leftForward, leftBackward, rightForward,
				rightBackward, backwardLeft, backwardRight);

		for (Position possibleMove : targetPositions) {
			Piece other = board.getPieceAt(possibleMove);
			Move move = new Move(getPosition(), possibleMove);

			if (other != null && other.isEnemyOf(this)) {
				validMoves.add(move);
			}

			if (other == null && !board.isOutOfBounds(possibleMove)) {
				validMoves.add(move);
			}
		}

		return validMoves;
	}
}
