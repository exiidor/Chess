package softwareschreiber.chessengine;

import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;

public abstract class Game {
	private Board board;
	private boolean isWhitesTurn;
	private boolean gameIsActive;

	public Game() {
		board = new Board(this);
		isWhitesTurn = true;
		gameIsActive = true;
	}

	public Game(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

	void startGame() {
		board.initializeStartingPositions();

		board.addPieceMovedListener((piece, move) -> {
			isWhitesTurn = !isWhitesTurn;
			board.checkForMates(piece);
		});
		board.addMoveUndoneListener((piece, move) -> {
			isWhitesTurn = !isWhitesTurn;
			board.checkForMates(piece);
		});
	}

	protected abstract Piece getPromotionTarget(Board board, Pawn pawn);

	protected abstract void checkMate(String color);

	protected abstract void staleMate();

	public void timeForTurn(boolean isWhitesTurn) {
		// Whites time for move
	}

	protected void endGame() {
		gameIsActive = false;
	}

	public boolean isGameActive() {
		return gameIsActive;
	}
}
