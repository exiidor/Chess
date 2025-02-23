package softwareschreiber.chessengine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;

public abstract class Game {
	private final Board board;
	private final List<Consumer<Boolean>> gameEndListeners;
	private boolean isWhitesTurn;
	private boolean gameOver = false;

	public Game() {
		board = new Board(this);
		gameEndListeners = new ArrayList<>();
		isWhitesTurn = true;
	}

	public Board getBoard() {
		return board;
	}

	public void startGame() {
		board.initializeStartingPositions();

		board.addSubmittedMoveDoneListener((piece, move) -> {
			board.checkForEnemyMates(piece);

			if (!gameOver) {
				isWhitesTurn = !piece.isWhite();
			}
		});
		board.addSubmittedUndoMoveDoneListener((piece, move) -> {
			board.checkForEnemyMates(piece);

			if (!gameOver) {
				isWhitesTurn = piece.isWhite();
			}
		});
	}

	public void addGameEndListener(Consumer<Boolean> listener) {
		gameEndListeners.add(listener);
	}

	protected abstract Piece getPromotionTarget(Board board, Pawn pawn);

	protected abstract void checkMate(String color);

	protected abstract void staleMate();

	protected void endGame() {
		gameOver = true;
		gameEndListeners.forEach(listener -> listener.accept(isWhitesTurn));
	}

	public boolean isWhitesTurn() {
		return isWhitesTurn;
	}

	public boolean isTimeForTurn(Piece piece) {
		return piece.isWhite() == isWhitesTurn;
	}

	public boolean isGameOver() {
		return gameOver;
	}
}
