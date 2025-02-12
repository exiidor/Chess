package softwareschreiber.chessengine.gamepieces;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.CastlingMove;
import softwareschreiber.chessengine.move.Move;

public class King extends Piece {
	public King(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	public boolean isChecked() {
		return board.getAllEnemyMovesExeptKingMoves(this).stream()
				.map(Move::getTargetPos)
				.anyMatch(pos -> pos.equals(getPosition()));
	}

	@Override
	public String getName() {
		return "King";
	}

	@Override
	public Set<? extends Move> getValidMovesInternal() {
		Set<Move> validMoves = new LinkedHashSet<>();

		// Normal moves for King

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

		// Casting

		if (!hasMoved() && !isChecked()) {
			Piece leftPiece = board.getPieceAt(0, getY());
			Piece rightPiece = board.getPieceAt(7, getY());

			if (leftPiece != null && leftPiece instanceof Rook leftRook) {
				boolean canCastle = !leftRook.hasMoved()
						&& board.getPieceAt(1, getY()) == null
						&& board.getPieceAt(2, getY()) == null
						&& board.getPieceAt(3, getY()) == null
						&& !board.getAllEnemyMoves(this).stream()
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
						&& !board.getAllEnemyMoves(this).stream()
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
}
