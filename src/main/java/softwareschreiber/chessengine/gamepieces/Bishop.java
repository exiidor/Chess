package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.evaluation.EvaluationCharts;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.Move;

public class Bishop extends Piece {
	public Bishop(PieceColor color, Board board) {
		super(color, board);
	}

	@Override
	public String getName() {
		return "Bishop";
	}

	@Override
	public int getValue() {
		return 3;
	}

	@Override
	public int[][] evaluationChart() {
		return EvaluationCharts.bishopTable;
	}

	@Override
	public int getMaxMoves() {
		return 13;
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
			for (int xDirection = -1; xDirection <= 1; xDirection += 2) {
				for (int i = 1; i < 8; i++) {
					Position targetPos = new Position(getX() + i * xDirection, getY() + i * yDirection);

					if (board.isOutOfBounds(targetPos)) {
						break;
					}

					Piece other = board.getPieceAt(targetPos);

					if (other == null) {
						validMoves.add(new Move(getPosition(), targetPos));
					} else {
						if (other.isEnemyOf(this)) {
							validMoves.add(new CaptureMove(getPosition(), targetPos, other));
						}

						break;
					}
				}
			}
		}

		return validMoves;
	}

	@Override
	public Piece copyWith(Board board) {
		return new Bishop(color, board);
	}
}
