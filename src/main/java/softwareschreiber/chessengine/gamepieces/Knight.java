package softwareschreiber.chessengine.gamepieces;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.evaluation.EvaluationCharts;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.Move;

public class Knight extends Piece {
	public Knight(PieceColor color, Board board) {
		super(color, board);
	}

	@Override
	public String getName() {
		return "Knight";
	}

	@Override
	public char getSymbol() {
		return 'k';
	}

	@Override
	public int getValue() {
		return 3;
	}

	@Override
	public int[][] evaluationChart() {
		return EvaluationCharts.knightTable;
	}

	@Override
	public Set<? extends Move> getValidMovesInternal() {
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

		for (Position targetPos : targetPositions) {
			Piece other = board.getPieceAt(targetPos);

			if (other != null && other.isEnemyOf(this)) {
				validMoves.add(new CaptureMove(getPosition(), targetPos, other));
			}

			if (other == null && !board.isOutOfBounds(targetPos)) {
				validMoves.add(new Move(getPosition(), targetPos));
			}
		}

		return validMoves;
	}
}
