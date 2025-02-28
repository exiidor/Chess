package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.evaluation.EvaluationCharts;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.EnPassantMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.util.Pair;

public class Pawn extends Piece {
	public Pawn(PieceColor color, Board board) {
		super(color, board);
	}

	@Override
	public String getName() {
		return "Pawn";
	}

	@Override
	public int getValue() {
		return 1;
	}

	@Override
	public int[][] evaluationChart() {
		return EvaluationCharts.pawnTable;
	}

	public int getDirection() {
		return isWhite() ? 1 : -1;
	}

	@Override
	public Set<? extends Move> getValidMovesInternal() {
		Set<Move> moves = new LinkedHashSet<>();
		boolean canMoveTwo;

		if (isWhite()) {
			canMoveTwo = getY() == 1;
		} else {
			canMoveTwo = getY() == 6;
		}

		Position forwardPos = new Position(getX(), getY() + getDirection());
		Position forwardByTwoPos = new Position(getX(), getY() + getDirection() * 2);

		if (board.getPieceAt(forwardPos) == null) {
			Move move = forwardPos.getY() == (isWhite() ? board.getMaxY() : board.getMinY())
					? new PromotionMove(getPosition(), forwardPos, null)
					: new Move(getPosition(), forwardPos);
			moves.add(move);

			if (canMoveTwo && board.getPieceAt(forwardByTwoPos) == null) {
				moves.add(new Move(getPosition(), forwardByTwoPos));
			}
		}

		Position forwardLeftPos = new Position(getX() - 1, getY() + getDirection());
		Position forwardRightPos = new Position(getX() + 1, getY() + getDirection());
		Piece forwardLeftPiece = board.getPieceAt(forwardLeftPos);
		Piece forwardRightPiece = board.getPieceAt(forwardRightPos);

		if (forwardLeftPiece != null && forwardLeftPiece.isEnemyOf(this)) {
			Move move = forwardLeftPiece.getY() == (isWhite() ? board.getMaxY() : board.getMinY())
					? new PromotionMove(getPosition(), forwardLeftPos, forwardLeftPiece)
					: new CaptureMove(getPosition(), forwardLeftPos, forwardLeftPiece);
			moves.add(move);
		}

		if (forwardRightPiece != null && forwardRightPiece.isEnemyOf(this)) {
			Move move = forwardRightPiece.getY() == (isWhite() ? board.getMaxY() : board.getMinY())
					? new PromotionMove(getPosition(), forwardRightPos, forwardRightPiece)
					: new CaptureMove(getPosition(), forwardRightPos, forwardRightPiece);
			moves.add(move);
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

	@Override
	public Piece copyWith(Board board) {
		return new Pawn(color, board);
	}
}
