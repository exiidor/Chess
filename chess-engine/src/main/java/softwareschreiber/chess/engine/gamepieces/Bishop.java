package softwareschreiber.chess.engine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.evaluation.EvaluationCharts;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.NormalMove;

public class Bishop extends Piece {
	public Bishop(PieceColor color, Board board) {
		super(color, board);
	}

	@Override
	public String getName() {
		return "Bishop";
	}

	@Override
	public char getSymbol() {
		return isWhite() ? '♗' : '♝';
	}

	@Override
	public int getValue() {
		return 3;
	}

	@Override
	public int[][] getEvaluationChart() {
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
						validMoves.add(new NormalMove(getPosition(), targetPos));
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
