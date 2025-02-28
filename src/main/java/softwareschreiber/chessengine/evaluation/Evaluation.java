package softwareschreiber.chessengine.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.util.Pair;

public class Evaluation {
	private Board board;

	public Evaluation(Board board) {
		this.board = board;
	}

	@Deprecated
	public int absoluteMaterialEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += piece.getValue();
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.getValue();
		}

		return score;
	}

	public int relativeMaterialEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += piece.evaluationChart()[piece.getX()][7 - piece.getY()];
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.evaluationChart()[piece.getX()][piece.getY()];
		}

		return score;
	}

	public int mobilityEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.getValidMovesInternal().size());
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.getValidMovesInternal().size());
		}

		return score;
	}

	public int evaluate() {
		return absoluteMaterialEvaluation() + mobilityEvaluation();
	}

	private int minMax(int depth, int alpha, int beta, PieceColor color) {
		if (depth == 0) {
			return evaluate();
		}

		if (color == PieceColor.WHITE) {
			int max = Integer.MIN_VALUE;

			for (Piece piece : board.getPieces(color)) {
				Set<? extends Move> validMoves = piece.getValidMoves();

				for (Move move : validMoves) {
					board.move(piece, move, true);

					int score = minMax(depth - 1, alpha, beta, color.getOpposite());

					board.undo(true);

					max = Math.max(max, score);
					alpha = Math.max(alpha, score);

					if (alpha >= beta) {
						break;
					}
				}
			}

			return max;
		} else {
			int min = Integer.MAX_VALUE;

			for (Piece piece : board.getPieces(color)) {
				Set<? extends Move> validMoves = piece.getValidMoves();

				for (Move move : validMoves) {
					board.move(piece, move, true);

					int score = minMax(depth - 1, alpha, beta, color.getOpposite());

					board.undo(true);

					min = Math.min(min, score);
					beta = Math.min(beta, score);

					if (alpha >= beta) {
						break;
					}
				}
			}

			return min;
		}
	}

	public Move bestMove(int depth, PieceColor color) {
		int max = Integer.MIN_VALUE;
		Move bestMove = null;

		for (Piece piece : board.getPieces(color)) {
			Set<? extends Move> validMoves = piece.getValidMoves();
			List<CompletableFuture<Pair<Integer, Move>>> futures = new ArrayList<>();

			for (Move move : validMoves) {
				futures.add(CompletableFuture.supplyAsync(() -> {
					Board copiedBoard = this.board.copy();
					Piece copiedPiece = copiedBoard.getPieceAt(move.getSourcePos());
					Move copiedMove = move.copyWith(copiedBoard);

					copiedBoard.move(copiedPiece, copiedMove, true);

					int score = -new Evaluation(copiedBoard).minMax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.getOpposite());

					copiedBoard.undo(true);

					return Pair.of(score, move);
				}));
			}

			for (CompletableFuture<Pair<Integer, Move>> future : futures) {
				Pair<Integer, Move> pair = future.join();
				int score = pair.getLeft();
				Move move = pair.getRight();

				if (score > max) {
					max = score;
					bestMove = move;
				}
			}
		}

		return bestMove;
	}
}
