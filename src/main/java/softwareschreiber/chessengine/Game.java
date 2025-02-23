package softwareschreiber.chessengine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;

public abstract class Game {
	private final Board board;
	private final List<Consumer<PieceColor>> gameEndListeners;
	private PieceColor activeColor;
	private boolean gameOver = false;

	public Game() {
		board = new Board(this);
		gameEndListeners = new ArrayList<>();
		activeColor = PieceColor.WHITE;
	}

	public Board getBoard() {
		return board;
	}

	public void startGame() {
		board.initializeStartingPositions();

		board.addSubmittedMoveDoneListener((piece, move) -> {
			board.checkForEnemyMates(piece);

			if (!gameOver) {
				activeColor = piece.getColor().getOpposite();
			}
		});
		board.addSubmittedUndoMoveDoneListener((piece, move) -> {
			board.checkForEnemyMates(piece);

			if (!gameOver) {
				activeColor = piece.getColor();
			}
		});
	}

	public void addGameEndListener(Consumer<PieceColor> listener) {
		gameEndListeners.add(listener);
	}

	protected abstract Piece getPromotionTarget(Board board, Pawn pawn);

	protected abstract void checkMate(String color);

	protected abstract void staleMate();

	protected void endGame() {
		gameOver = true;
		gameEndListeners.forEach(listener -> listener.accept(activeColor));
	}

	public boolean isWhitesTurn() {
		return activeColor == PieceColor.WHITE;
	}

	public boolean isTimeForTurn(Piece piece) {
		return piece.getColor() == activeColor;
	}

	public boolean isGameOver() {
		return gameOver;
	}
}
