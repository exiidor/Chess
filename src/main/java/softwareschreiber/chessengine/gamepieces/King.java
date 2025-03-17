package softwareschreiber.chessengine.gamepieces;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.evaluation.EvaluationCharts;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.CastlingMove;
import softwareschreiber.chessengine.move.Move;

public class King extends Piece {
	public King(PieceColor color, Board board) {
		super(color, board);
	}

	public boolean isChecked() {
		for (Move move : board.getEnemyMovesExceptKingMoves(this)) {
			Position targetPos = move.getTargetPos();

			if (targetPos.equals(getPosition())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return "King";
	}

	@Override
	public int getValue() {
		return 0;
	}

	@Override
	public int[][] evaluationChart() {
		return EvaluationCharts.kingTable;
	}

	@Override
	public int getMaxMoves() {
		return 8;
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<? extends Move> standardMoves = getStandardMoves();
		Set<? extends Move> castlingMoves = getCastlingMoves();

		Set<Move> validMoves = LinkedHashSet.newLinkedHashSet(standardMoves.size() + castlingMoves.size());
		validMoves.addAll(standardMoves);
		validMoves.addAll(castlingMoves);

		return validMoves;
	}

	public Set<Move> getStandardMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		Position forward = new Position(getX(), getY() + 1);
		Position forwardRight = new Position(getX() + 1, getY() + 1);
		Position right = new Position(getX() + 1, getY());
		Position backRight = new Position(getX() + 1, getY() - 1);
		Position back = new Position(getX(), getY() - 1);
		Position backLeft = new Position(getX() - 1, getY() - 1);
		Position left = new Position(getX() - 1, getY());
		Position forwardLeft = new Position(getX() - 1, getY() + 1);

		List<Position> targetPositions = Arrays.asList(forward, forwardRight, right, backRight, back, backLeft, left,
				forwardLeft);

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

	public Set<Move> getCastlingMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		if (!hasMoved() && !isChecked()) {
			Piece leftPiece = board.getPieceAt(0, getY());
			Piece rightPiece = board.getPieceAt(7, getY());

			if (leftPiece != null && leftPiece instanceof Rook leftRook) {
				boolean canCastle = !leftRook.hasMoved()
						&& board.getPieceAt(1, getY()) == null
						&& board.getPieceAt(2, getY()) == null
						&& board.getPieceAt(3, getY()) == null
						&& !board.getEnemyMovesExceptKingMoves(this).stream()
								.map(Move::getTargetPos)
								.anyMatch(pos -> pos.equals(new Position(2, getY()))
										|| pos.equals(new Position(3, getY())));

				if (canCastle) {
					validMoves.add(new CastlingMove(
							getPosition(),
							new Position(2, getY()),
							leftRook,
							new Position(3, getY())));
				}
			}

			if (rightPiece != null && rightPiece instanceof Rook rightRook) {
				boolean canCastle = !rightRook.hasMoved()
						&& board.getPieceAt(5, getY()) == null
						&& board.getPieceAt(6, getY()) == null
						&& !board.getEnemyMovesExceptKingMoves(this).stream()
								.map(Move::getTargetPos)
								.anyMatch(pos -> pos.equals(new Position(5, getY()))
										|| pos.equals(new Position(6, getY())));

				if (canCastle) {
					validMoves.add(new CastlingMove(
							getPosition(),
							new Position(6, getY()),
							rightRook,
							new Position(5, getY())));
				}
			}
		}

		return validMoves;
	}

	@Override
	public Piece copyWith(Board board) {
		return new King(color, board);
	}
}
