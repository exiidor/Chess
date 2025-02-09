package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.EnPassantMove;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;
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
