package softwareschreiber.chess.engine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.evaluation.EvaluationCharts;
import softwareschreiber.chess.engine.history.HistoryEntry;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.EnPassantMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

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

	@Override
	public int getMaxMoves() {
		return 4;
	}

	public int getDirection() {
		return isWhite() ? 1 : -1;
	}

	@Override
	public Set<? extends Move> getValidMoves() {
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
			if (forwardPos.getY() == (isWhite() ? board.getMaxY() : board.getMinY())) {
				moves.addAll(getAllPossiblePromotionMoves(getPosition(), forwardPos, null));
			} else {
				moves.add(new Move(getPosition(), forwardPos));
			}

			if (canMoveTwo && board.getPieceAt(forwardByTwoPos) == null) {
				moves.add(new Move(getPosition(), forwardByTwoPos));
			}
		}

		Position forwardLeftPos = new Position(getX() - 1, getY() + getDirection());
		Position forwardRightPos = new Position(getX() + 1, getY() + getDirection());
		Piece forwardLeftPiece = board.getPieceAt(forwardLeftPos);
		Piece forwardRightPiece = board.getPieceAt(forwardRightPos);

		if (forwardLeftPiece != null && forwardLeftPiece.isEnemyOf(this)) {
			if (forwardLeftPiece.getY() == (isWhite() ? board.getMaxY() : board.getMinY())) {
				moves.addAll(getAllPossiblePromotionMoves(getPosition(), forwardLeftPos, forwardLeftPiece));
			} else {
				moves.add(new CaptureMove(getPosition(), forwardLeftPos, forwardLeftPiece));
			}
		}

		if (forwardRightPiece != null && forwardRightPiece.isEnemyOf(this)) {
			if (forwardRightPiece.getY() == (isWhite() ? board.getMaxY() : board.getMinY())) {
				moves.addAll(getAllPossiblePromotionMoves(getPosition(), forwardRightPos, forwardRightPiece));
			} else {
				moves.add(new CaptureMove(getPosition(), forwardRightPos, forwardRightPiece));
			}
		}

		// En Passant

		HistoryEntry historyItem = board.getHistory().getCurrent();
		Piece enemyPiece = historyItem.getPiece();
		Move enemyMove = historyItem.getMove();

		if (enemyPiece instanceof Pawn && Math.abs(enemyMove.getSourcePos().getY() - enemyMove.getTargetPos().getY()) == 2) {
			Position left = new Position(getX() - 1, getY());
			Position right = new Position(getX() + 1, getY());
			Piece leftPiece = board.getPieceAt(left);
			Piece rightPiece = board.getPieceAt(right);

			if (leftPiece instanceof Pawn leftPawn && leftPawn.equals(enemyPiece)) {
				if (leftPiece.isEnemyOf(this)) {
					moves.add(new EnPassantMove(getPosition(), forwardLeftPos, leftPawn));
				}
			}

			if (rightPiece instanceof Pawn rightPawn && rightPawn.equals(enemyPiece)) {
				if (rightPiece.isEnemyOf(this)) {
					moves.add(new EnPassantMove(getPosition(), forwardRightPos, rightPawn));
				}
			}
		}

		return moves;
	}

	private Set<? extends Move> getAllPossiblePromotionMoves(Position position, Position targetPosition, Piece captured) {
		Set<Move> moves = new LinkedHashSet<>();
		Piece replacement;

		for (Class<? extends Piece> pieceClass : board.getGame().getAllowedPromotionTargets()) {
			if (pieceClass == Knight.class) {
				replacement = new Knight(color, board);
			} else if (pieceClass == Bishop.class) {
				replacement = new Bishop(color, board);
			} else if (pieceClass == Rook.class) {
				replacement = new Rook(color, board);
			} else if (pieceClass == Queen.class) {
				replacement = new Queen(color, board);
			} else {
				throw new IllegalStateException();
			}

			moves.add(new PromotionMove(position, targetPosition, captured, replacement));
		}

		return moves;
	}

	@Override
	public Piece copyWith(Board board) {
		return new Pawn(color, board);
	}
}
