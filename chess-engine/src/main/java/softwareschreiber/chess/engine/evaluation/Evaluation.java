package softwareschreiber.chess.engine.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.MateKind;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.util.Pair;

/**
 * Estimates the chance of a given player winning based on the current board state
 * and can compute the best next move.
 */
public class Evaluation {
	private final int variant;
	private Game game;
	private Board board;

	/**
	 * @param game The current game instance.
	 * @param board The current board instance.
	 */
	public Evaluation(Game game, Board board) {
		this.game = game;
		this.board = board;
		variant = 1;
	}

	/**
	 * Evaluates the current board state based on the selected evaluation function.
	 *
	 * @return The evaluation score for the selected evaluation function.
	 */
	public int evaluate() {
		switch (variant) {
			case 1:
				return defaultEvaluation();
			case 2:
				return absoluteMaterialEvaluation();
			case 3:
				return relativeMaterialEvaluation();
			case 4:
				return mobilityEvaluation();
			case 5:
				return randomEvaluation();
			default:
				throw new IllegalArgumentException("Evaluation function " + variant + " does not exist.");
		}
	}

	/**
	 * Evaluates the position using absolute material evaluation, mobility evaluation, relative mobility evaluation, and enemy in checkmate.
	 *
	 * @return The evaluation score for the position.
	 */
	private int defaultEvaluation() {
		return absoluteMaterialEvaluation() * 2 + mobilityEvaluation() + relativeMobilityEvaluation() + enemyInCheckMate();
	}

	/**
	 * Counts all pieces on the board and adds their values, subtracting the opponent's pieces.
	 *
	 * @return The total material value of the board.
	 */
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

	/**
	 * Evaluates the position based on the relative material value of each piece, using the evaluation chart.
	 *
	 * @return The total relative material value of the board.
	 */
	public int relativeMaterialEvaluation() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += piece.getEvaluationChart()[board.getMaxY() - piece.getY()][piece.getX()];
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.getEvaluationChart()[piece.getY()][piece.getX()];
		}

		return score;
	}

	/**
	 * Evaluates the position based on the mobility of each piece.
	 *
	 * @return The total mobility value of the board.
	 */
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

	/**
	 * Evaluates the position based on the relative mobility of each piece.
	 *
	 * @implSpec Divides the number of valid moves by the maximum number of moves for each piece.
	 *
	 * @return The total relative mobility value of the board.
	 */
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

	/**
	 * Evaluates the position based on the absolute and relative material value of each piece.
	 *
	 * @return The total absolute multiplied by the relative material value of the board for each piece.
	 */
	public int absoluteAndRelativeMaterialEvaluation() {
		double score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.getEvaluationChart()[board.getMaxY() - piece.getY()][piece.getX()] / 100) * piece.getValue();
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.getEvaluationChart()[piece.getY()][piece.getX()] / 100) * piece.getValue();
		}

		return (int) Math.round(score);
	}

	/**
	 * Evaluates the position based on the relative material value of each piece multiplied by
	 * the piece's value and the number of valid moves, divided by the maximum number of moves,
	 * as well as whether the piece is under attack.
	 *
	 * @return The evaluation score for the position.
	 */
	public int everythingEvaluation() {
		double score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += (piece.getEvaluationChart()[board.getMaxY() - piece.getY()][piece.getX()] / 100) * piece.getValue() * piece.getValidMoves().size()/piece.getMaxMoves() * (piece.isUnderAttack() ? 0.2 : 1);
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= (piece.getEvaluationChart()[piece.getY()][piece.getX()] / 100) * piece.getValue() * piece.getValidMoves().size()/piece.getMaxMoves() * (piece.isUnderAttack() ? 0.2 : 1);
		}

		return (int) Math.round(score);
	}

	/**
	 * Evaluates the position based on whether the enemy king is in checkmate.
	 *
	 * @return A large positive value if the enemy king is in checkmate,
	 * a large negative value if the player's king is in checkmate, or 0 otherwise.
	 */
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

	/**
	 * Generates a random evaluation score.
	 *
	 * @return A random evaluation score.
	 */
	private int randomEvaluation() {
		return (int) (Math.random() * 1000);
	}

	/**
	 * Minimax algorithm with alpha-beta pruning.
	 *
	 * @param depth The current depth in the game tree.
	 * @param alpha The best score that the maximizer currently can guarantee at this level or above.
	 * @param beta  The best score that the minimizer currently can guarantee at this level or above.
	 * @param color The color of the player whose turn it is to move.
	 * @return The evaluation score for the current board state.
	 */
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

	/**
	 * Finds the best move for the given color using a multi-threaded approach.
	 *
	 * @param depth The depth to search for the best move.
	 * @param color The color of the player whose turn it is to move.
	 * @return The best move found.
	 */
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

	/**
	 * Compares two moves based on their type (capture, promotion, etc.).
	 * This is for better pruning of the search tree.
	 *
	 * @param moveA The first move to compare.
	 * @param moveB The second move to compare.
	 * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 */
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
