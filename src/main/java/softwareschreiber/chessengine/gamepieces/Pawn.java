package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.EnPassantMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.util.Pair;

public class Pawn extends Piece {
	public Pawn(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public String getName() {
		return "Pawn";
	}

	public int getDirection() {
		return isWhite ? 1 : -1;
	}

	@Override
	public Set<? extends Move> getValidMovesInternal() {
		Set<Move> moves = new LinkedHashSet<>();
		boolean canMoveTwo;

		if (isWhite) {
			canMoveTwo = getY() == 1;
		} else {
			canMoveTwo = getY() == 6;
		}

		Position forwardPos = new Position(getX(), getY() + getDirection());
		Position forwardByTwoPos = new Position(getX(), getY() + getDirection() * 2);

		if (board.getPieceAt(forwardPos) == null) {
			moves.add(new Move(getPosition(), forwardPos));

			if (canMoveTwo && board.getPieceAt(forwardByTwoPos) == null) {
				moves.add(new Move(getPosition(), forwardByTwoPos));
			}
		}

		Position forwardLeftPos = new Position(getX() - 1, getY() + getDirection());
		Position forwardRightPos = new Position(getX() + 1, getY() + getDirection());
		Piece forwardLeftPiece = board.getPieceAt(forwardLeftPos);
		Piece forwardRightPiece = board.getPieceAt(forwardRightPos);

		if (forwardLeftPiece != null && forwardLeftPiece.isEnemyOf(this)) {
			moves.add(new CaptureMove(getPosition(), forwardLeftPos, forwardLeftPiece));
		}

		if (forwardRightPiece != null && forwardRightPiece.isEnemyOf(this)) {
			moves.add(new CaptureMove(getPosition(), forwardRightPos, forwardRightPiece));
		}

		// En Passant

		Pair<Piece, Move> historyItem = board.getHistory().getCurrent();
		Piece enemyPiece = historyItem.getLeft();
		Move enemyMove = historyItem.getRight();

		if (enemyPiece instanceof Pawn && Math.abs(enemyMove.getSourcePos().getY() - enemyMove.getTargetPos().getY()) == 2) {
			Position left = new Position(getX() - 1, getY());
			Position right = new Position(getX() + 1, getY());
			Piece leftPiece = board.getPieceAt(left);
			Piece rightPiece = board.getPieceAt(right);

			if (leftPiece instanceof Pawn leftPawn && leftPawn.equals(enemyPiece)) {
				if (leftPawn != null && leftPiece.isEnemyOf(this)) {
					moves.add(new EnPassantMove(getPosition(), forwardLeftPos, leftPawn));
				}
			}

			if (rightPiece instanceof Pawn rightPawn && rightPawn.equals(enemyPiece)) {
				if (rightPawn != null && rightPiece.isEnemyOf(this)) {
					moves.add(new EnPassantMove(getPosition(), forwardRightPos, rightPawn));
				}
			}
		}

		return moves;
	}
}
