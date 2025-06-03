package softwareschreiber.chess.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import softwareschreiber.chess.engine.gamepieces.Bishop;
import softwareschreiber.chess.engine.gamepieces.Knight;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.gamepieces.Queen;
import softwareschreiber.chess.engine.gamepieces.Rook;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.ComputerPlayer;
import softwareschreiber.chess.engine.player.HumanPlayer;
import softwareschreiber.chess.engine.player.Player;
import softwareschreiber.chess.engine.player.SimulationPlayer;

/**
 * Represents a chess game. Manages the game state, including
 * the board, players, and game flow. Also handles game events such as
 * checkmate and stalemate.
 */
public abstract class Game {
	private final Board board;
	private final List<Consumer<PieceColor>> gameEndListeners;
	private final Player whitePlayer;
	private final Player blackPlayer;
	private PieceColor activeColor;
	private boolean gameOver = false;
	private SimulationPlayer whiteSimulationPlayer;
	private SimulationPlayer blackSimulationPlayer;

	/**
	 * Constructs a new instance with the specified starting color, one human and one computer player.
	 *
	 * @param startingColor The color of the player who starts the game.
	 */
	public Game(PieceColor startingColor) {
		board = new Board(this);
		gameEndListeners = new ArrayList<>();
		activeColor = startingColor;
		whitePlayer = new HumanPlayer(PieceColor.WHITE, this);
		blackPlayer = new ComputerPlayer(PieceColor.BLACK);
	}

	/**
	 * Constructs a new instance with white as the starting color, one human and one computer player.
	 */
	public Game() {
		this(PieceColor.WHITE);
	}

	/**
	 * Constructs a new instance with the specified player types. White is always first here.
	 *
	 * @param hasComputerOpponent If true, the black player is a computer player; otherwise, it's a human player.
	 */
	public Game(boolean hasComputerOpponent) {
		board = new Board(this);
		gameEndListeners = new ArrayList<>();
		whitePlayer = new HumanPlayer(PieceColor.WHITE, this);
		activeColor = PieceColor.WHITE;

		if (hasComputerOpponent) {
			blackPlayer = new ComputerPlayer(PieceColor.BLACK);
		} else {
			blackPlayer = new HumanPlayer(PieceColor.BLACK, this);
		}
	}

	/**
	 * Constructs a new instance with the specified player types and starting color.
	 *
	 * @param hasComputerOpponent If true, the black player is a computer player; otherwise, it's a human player.
	 * @param startingColor The color of the player who starts the game.
	 */
	public Game(boolean hasComputerOpponent, PieceColor startingColor) {
		board = new Board(this);
		gameEndListeners = new ArrayList<>();
		whitePlayer = new HumanPlayer(PieceColor.WHITE, this);
		activeColor = startingColor;

		if (hasComputerOpponent) {
			blackPlayer = new ComputerPlayer(PieceColor.BLACK);
		} else {
			blackPlayer = new HumanPlayer(PieceColor.BLACK, this);
		}
	}

	public Board getBoard() {
		return board;
	}

	public void setIsWhitesTurn(boolean isWhitesTurn) {
		if (isWhitesTurn) {
			activeColor = PieceColor.WHITE;
		} else {
			activeColor = PieceColor.BLACK;
		}
	}

	public Player getWhitePlayer() {
		return whitePlayer;
	}

	public Player getBlackPlayer() {
		return blackPlayer;
	}

	public Player getPlayer(PieceColor color) {
		return color == PieceColor.WHITE
				? getWhitePlayer()
				: getBlackPlayer();
	}

	public SimulationPlayer getWhiteSimulationPlayer() {
		if (whiteSimulationPlayer == null) {
			whiteSimulationPlayer = new SimulationPlayer(PieceColor.WHITE);
		}

		return whiteSimulationPlayer;
	}

	public SimulationPlayer getBlackSimulationPlayer() {
		if (blackSimulationPlayer == null) {
			blackSimulationPlayer = new SimulationPlayer(PieceColor.BLACK);
		}

		return blackSimulationPlayer;
	}

	public SimulationPlayer getSimulationPlayer(PieceColor color) {
		return color == PieceColor.WHITE
				? getWhiteSimulationPlayer()
				: getBlackSimulationPlayer();
	}

	/**
	 * Starts the game by initializing the board and setting up event listeners for moves.
	 */
	public void startGame() {
		board.initializeStartingPositions();

		board.addSubmittedMoveDoneListener((piece, move) -> {
			checkForMate(piece.getColor().getOpposite());

			if (!gameOver) {
				activeColor = piece.getColor().getOpposite();
			}
		});
		board.addSubmittedUndoMoveDoneListener((piece, move) -> {
			if (!gameOver) {
				activeColor = piece.getColor();
			}
		});
	}

	private void checkForMate(PieceColor color) {
		switch (board.checkForMate(color)) {
			case CHECKMATE:
				checkMate(color.getOpposite());
				break;
			case STALEMATE:
				staleMate();
				break;
			case null:
				break;
		}
	}

	public void addGameEndListener(Consumer<PieceColor> listener) {
		gameEndListeners.add(listener);
	}

	public abstract PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves);

	protected abstract void checkMate(PieceColor winningColor);

	protected abstract void staleMate();

	protected void endGame() {
		gameOver = true;
		gameEndListeners.forEach(listener -> listener.accept(activeColor));
	}

	public PieceColor getActiveColor() {
		return activeColor;
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

	// TODO
	public List<Class<? extends Piece>> getAllowedPromotionTargets() {
		List<Class<? extends Piece>> pieces = new ArrayList<>();

		pieces.add(Bishop.class);
		pieces.add(Knight.class);
		pieces.add(Queen.class);
		pieces.add(Rook.class);

		return pieces;
	}
}
