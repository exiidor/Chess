package softwareschreiber.chessengine.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Game;
import softwareschreiber.chessengine.MateKind;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.util.Pair;

public class Evaluation {
	private Game game;
	private Board board;

	public Evaluation(Game game, Board board) {
		this.game = game;
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
			score += piece.evaluationChart()[board.getMaxY() - piece.getY()][piece.getX()];
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.evaluationChart()[piece.getY()][piece.getX()];
		}

		return score;
	}

	public int mobilityEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.getValidMoves().size());
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.getValidMoves().size());
		}

		return score;
	}

	public int relativeMobilityEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.getValidMoves().size()/piece.getMaxMoves());
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.getValidMoves().size()/piece.getMaxMoves());
		}

		return score;
	}

	public int absoluteAndRelativeMaterialEvaluation() {
		double score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.evaluationChart()[board.getMaxY() - piece.getY()][piece.getX()] / 100) * piece.getValue();
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.evaluationChart()[piece.getY()][piece.getX()] / 100) * piece.getValue();
		}

		return (int) Math.round(score);
	}

	public int everythingEvaluation() {
		double score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.evaluationChart()[board.getMaxY() - piece.getY()][piece.getX()] / 100) * piece.getValue() * piece.getValidMoves().size()/piece.getMaxMoves() * (piece.isUnderAttack() ? 0.2 : 1);
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.evaluationChart()[piece.getY()][piece.getX()] / 100) * piece.getValue() * piece.getValidMoves().size()/piece.getMaxMoves() * (piece.isUnderAttack() ? 0.2 : 1);
		}

		return (int) Math.round(score);
	}

	public int enemyInCheckMate() {
		if (board.getKing(PieceColor.WHITE) == null || isInCheckMate(PieceColor.WHITE)) {
			return -100_000;
		} else if (board.getKing(PieceColor.BLACK) == null || isInCheckMate(PieceColor.BLACK)) {
			return 100_000;
		} else {
			return 0;
		}
	}

	private boolean isInCheckMate(PieceColor color) {
		return board.checkForMate(color) == MateKind.CHECKMATE;
	}

	public int evaluate() {
		return absoluteMaterialEvaluation() + relativeMobilityEvaluation() + enemyInCheckMate();
	}

	private int minMax(int depth, int alpha, int beta, PieceColor color) {
		if (depth == 0
				|| board.getKing(PieceColor.WHITE) == null
				|| board.getKing(PieceColor.BLACK) == null
				|| isInCheckMate(PieceColor.WHITE)
				|| isInCheckMate(PieceColor.BLACK)) {
			return evaluate();
		}

		if (color == PieceColor.WHITE) {
			int max = Integer.MIN_VALUE;

			for (Piece piece : board.getPieces(color)) {
				List<? extends Move> validMoves = new ArrayList<>(piece.getSafeMoves());
				validMoves.sort(this::compareMoves);

				for (Move move : validMoves) {
					board.move(piece, move, game.getSimulationPlayer(color));

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
				List<? extends Move> validMoves = new ArrayList<>(piece.getSafeMoves());
				validMoves.sort(this::compareMoves);

				for (Move move : validMoves) {
					board.move(piece, move, game.getSimulationPlayer(color));

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
		List<CompletableFuture<Pair<Integer, Move>>> futures = new ArrayList<>();
		int max = Integer.MIN_VALUE;
		Move bestMove = null;

		for (Piece piece : board.getPieces(color)) {
			Board board = this.board.copy();
			Set<? extends Move> validMoves = piece.getSafeMoves();

			for (Move move : validMoves) {
				futures.add(CompletableFuture.supplyAsync(() -> {
					Board copiedBoard = board.copy();
					Piece copiedPiece = copiedBoard.getPieceAt(move.getSourcePos());
					Move copiedMove = move.copyWith(copiedBoard);

					copiedBoard.move(copiedPiece, copiedMove, game.getSimulationPlayer(color));

					int score = -new Evaluation(game, copiedBoard).minMax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, color.getOpposite());

					copiedBoard.undo(true);

					return Pair.of(score, move);
				}));
			}
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

		return bestMove;
	}

	// Sorts better moves first
	private int compareMoves(Move moveA, Move moveB) {
		boolean isACapture = moveA instanceof CaptureMove;
		boolean isBCapture = moveB instanceof CaptureMove;
		boolean isAPromotion = moveA instanceof PromotionMove;
		boolean isBPromotion = moveB instanceof PromotionMove;

		if (isACapture && !isBCapture) {
			return -1;
		} else if (!isACapture && isBCapture) {
			return 1;
		} else if (isAPromotion && !isBPromotion) {
			return -1;
		} else if (!isAPromotion && isBPromotion) {
			return 1;
		} else {
			return 0;
		}
	}
}
