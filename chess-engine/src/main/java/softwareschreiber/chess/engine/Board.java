package softwareschreiber.chess.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.evaluation.Evaluation;
import softwareschreiber.chess.engine.gamepieces.Bishop;
import softwareschreiber.chess.engine.gamepieces.King;
import softwareschreiber.chess.engine.gamepieces.Knight;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.gamepieces.Queen;
import softwareschreiber.chess.engine.gamepieces.Rook;
import softwareschreiber.chess.engine.history.History;
import softwareschreiber.chess.engine.history.HistoryEntry;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.CastlingMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.NormalMove;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.Player;
import softwareschreiber.chess.engine.player.SimulationPlayer;

/**
 * A chess board.
 *
 * @implNote The board is represented as a 2D array of {@link Piece}s.
 */
public class Board {
	private final Game game;
	private final Piece[][] board;
	private final List<Piece> pieces;
	private final Map<Piece, Position> positions;
	private final History history;
	private final List<Consumer<Piece>> pieceAddedListeners;
	private final List<SubmittedMoveConsumer> submittedMoveListeners;
	private final List<PieceMovedConsumer> pieceMovedListeners;
	private final List<SubmittedUndoMoveDoneConsumer> submittedUndoMoveDoneListeners;
	private final List<MoveUndoneConsumer> moveUndoneListeners;

	/**
	 * Creates a new board with the default dimensions of 8x8.
	 *
	 * @param game The game this board belongs to.
	 */
	public Board(Game game) {
		this(game, 7, 7);
	}

	/**
	 * Creates a new board with the given dimensions.
	 *
	 * @param game The game this board belongs to.
	 * @param maxX The maximum x coordinate of the board, for dynamic board sizes.
	 * @param maxY The maximum y coordinate of the board, for dynamic board sizes.
	 */
	public Board(Game game, int maxX, int maxY) {
		this(game,
				new Piece[maxY + 1][maxX + 1],
				new ArrayList<>(),
				new HashMap<>(),
				new History(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>());
	}

	/**
	 * Copy constructor.
	 */
	private Board(
			Game game,
			Piece[][] board,
			List<Piece> pieces,
			Map<Piece, Position> positions,
			History history,
			List<Consumer<Piece>> pieceAddedListeners,
			List<SubmittedMoveConsumer> submittedMoveListeners,
			List<PieceMovedConsumer> pieceMovedListeners,
			List<SubmittedUndoMoveDoneConsumer> submittedUndoMoveDoneListeners,
			List<MoveUndoneConsumer> moveUndoneListeners) {
		this.game = game;
		this.board = board;
		this.pieces = pieces;
		this.positions = positions;
		this.history = history;
		this.pieceAddedListeners = pieceAddedListeners;
		this.submittedMoveListeners = submittedMoveListeners;
		this.pieceMovedListeners = pieceMovedListeners;
		this.submittedUndoMoveDoneListeners = submittedUndoMoveDoneListeners;
		this.moveUndoneListeners = moveUndoneListeners;
	}

	public void addPieceAddedListener(Consumer<Piece> listener) {
		pieceAddedListeners.add(listener);
	}

	public void addSubmittedMoveDoneListener(SubmittedMoveConsumer listener) {
		submittedMoveListeners.add(listener);
	}

	public void addPieceMovedListener(PieceMovedConsumer listener) {
		pieceMovedListeners.add(listener);
	}

	public void addSubmittedUndoMoveDoneListener(SubmittedUndoMoveDoneConsumer listener) {
		submittedUndoMoveDoneListeners.add(listener);
	}

	public void addMoveUndoneListener(MoveUndoneConsumer listener) {
		moveUndoneListeners.add(listener);
	}

	/**
	 * Initializes the starting positions of the pieces on the board.
	 */
	public void initializeStartingPositions() {
		for (int i = 0; i <= 1; i++) {
			boolean isWhite = i == 0;
			int y = isWhite ? getMinY() : getMaxY();
			PieceColor color = isWhite ? PieceColor.WHITE : PieceColor.BLACK;

			addPiece(0, y, new Rook(color, this));
			addPiece(1, y, new Knight(color, this));
			addPiece(2, y, new Bishop(color, this));
			addPiece(3, y, new Queen(color, this));
			addPiece(4, y, new King(color, this));
			addPiece(5, y, new Bishop(color, this));
			addPiece(6, y, new Knight(color, this));
			addPiece(7, y, new Rook(color, this));
			y = isWhite ? 1 : 6;

			for (int x = 0; x < 8; x++) {
				addPiece(x, y, new Pawn(color, this));
			}
		}
	}

	public <T extends Piece> T addPiece(Position position, T piece) {
		return addPiece(position.x(), position.y(), piece);
	}

	public <T extends Piece> T addPiece(int x, int y, T piece) {
		pieces.add(piece);
		positions.put(piece, new Position(x, y));
		board[y][x] = piece;

		pieceAddedListeners.forEach(listener -> listener.accept(piece));
		return piece;
	}

	/**
	 * Initiated by user input.
	 */
	public void move(Piece piece, Move move, Player player) {
		moveInternal(piece, move, player);

		if (!(player instanceof SimulationPlayer)) {
			submittedMoveListeners.forEach(listener -> listener.accept(piece, move));
		}
	}

	private void moveInternal(Piece piece, Move move, Player player) {
		Piece origPiece = piece;
		Position currentPosition = positions.get(piece);
		Position targetPosition = move.getTargetPos();

		if (!currentPosition.equals(move.getSourcePos())) {
			throw new IllegalArgumentException("Piece is not at source position");
		}

		// Special moves
		if (move instanceof CastlingMove castlingMove) {
			moveInternal(castlingMove.getOther(), castlingMove.getOtherMove(), player);
		} else if (move instanceof PromotionMove promotionMove) {
			Pawn pawn = (Pawn) piece;
			Piece replacement = promotionMove.getReplacement();

			assert replacement != null;

			if (promotionMove.getCaptured() != null) {
				capture(promotionMove.getCaptured());
			}

			pieces.remove(pawn);
			pieces.add(replacement);
			piece = replacement;
		} else if (move instanceof CaptureMove captureMove) {
			capture(captureMove.getCaptured());
		}

		// Throw error if target position is not empty
		assert getPieceAt(targetPosition) == null;

		// Update position
		board[currentPosition.y()][currentPosition.x()] = null;
		board[targetPosition.y()][targetPosition.x()] = piece;
		positions.put(piece, targetPosition);
		piece.onMoved(currentPosition, targetPosition);

		// History
		history.push(new HistoryEntry(player, origPiece, move));

		pieceMovedListeners.forEach(listener -> listener.accept(origPiece, move));
	}

	private void capture(Piece piece) {
		removePiece(piece);
	}

	public void removePiece(Piece piece) {
		Position position = positions.get(piece);
		pieces.remove(piece);
		board[position.y()][position.x()] = null;
	}

	public void undo(boolean virtual) {
		HistoryEntry lastMove = history.getCurrent();

		if (lastMove == null) {
			return;
		}

		if (lastMove.getPiece() == null || lastMove.getMove() == null) {
			throw new IllegalStateException("Last move is invalid");
		}

		undoMove(lastMove.getPiece(), lastMove.getMove(), virtual);
	}

	private void undoMove(Piece piece, Move move, boolean virtual) {
		undoMoveInternal(piece, move, virtual, true);

		if (!virtual) {
			submittedUndoMoveDoneListeners.forEach(listener -> listener.accept(piece, move));
		}
	}

	private void undoMoveInternal(Piece piece, Move move, boolean virtual, boolean modifyHistory) {
		Position currentPosition = move.getTargetPos();
		Position targetPosition = move.getSourcePos();

		board[currentPosition.y()][currentPosition.x()] = null;
		board[targetPosition.y()][targetPosition.x()] = piece;
		positions.put(piece, targetPosition);

		if (move instanceof CastlingMove castlingMove) {
			undoMoveInternal(castlingMove.getOther(), castlingMove.getOtherMove(), virtual, true);
		} else if (move instanceof PromotionMove promotionMove) {
			Piece replacement = promotionMove.getReplacement();
			Pawn originalPawn = (Pawn) piece;
			pieces.remove(replacement);
			positions.remove(replacement);
			board[currentPosition.y()][currentPosition.x()] = null;
			addPiece(targetPosition, originalPawn);

			if (promotionMove.getCaptured() != null) {
				undoMoveInternal(originalPawn,
						new CaptureMove(promotionMove.getSourcePos(), promotionMove.getTargetPos(),
								promotionMove.getCaptured()),
						virtual, false);
			}
		} else if (move instanceof CaptureMove captureMove) {
			Position capturedPos = captureMove.getCaptured().getPosition();
			addPiece(capturedPos, captureMove.getCaptured());
			undoMoveInternal(captureMove.getCaptured(), new NormalMove(capturedPos, capturedPos), virtual, false);
		}

		if (!history.canGoBack()) {
			throw new RuntimeException("Went back too far");
		}

		if (modifyHistory) {
			history.goBack();
		}

		moveUndoneListeners.forEach(listener -> listener.accept(piece, move));
		piece.onMoveUndone(currentPosition, targetPosition);
	}

	public Game getGame() {
		return game;
	}

	public int getMinX() {
		return 0;
	}

	public int getMinY() {
		return 0;
	}

	public int getMaxX() {
		return board[0].length - 1;
	}

	public int getMaxY() {
		return board.length - 1;
	}

	public Position getPosition(Piece piece) {
		return positions.get(piece);
	}

	public Piece getPieceAt(int x, int y) {
		if (x < getMinX() || x > getMaxX() || y < getMinY() || y > getMaxY()) {
			return null;
		}

		return board[y][x];
	}

	public Piece getPieceAt(Position position) {
		return getPieceAt(position.x(), position.y());
	}

	public Set<Piece> getPieces(PieceColor color) {
		Set<Piece> pieces = new HashSet<>();

		for (Piece piece : this.pieces) {
			if (piece.getColor() == color) {
				pieces.add(piece);
			}
		}

		return pieces;
	}

	public Set<Piece> getEnemyPieces(Piece piece) {
		Set<Piece> enemyPieces = new HashSet<>();

		for (Piece enemyPiece : pieces) {
			if (enemyPiece.isEnemyOf(piece)) {
				enemyPieces.add(enemyPiece);
			}
		}

		return enemyPieces;
	}

	public Set<? extends Move> getEnemyMoves(Piece piece) {
		Set<Move> enemyMoves = new HashSet<>();

		for (Piece enemyPiece : getEnemyPieces(piece)) {
			enemyMoves.addAll(enemyPiece.getSafeMoves());
		}

		return enemyMoves;
	}

	public Set<Move> getEnemyMovesExceptKingMoves(Piece piece) {
		Set<Move> enemyMoves = new HashSet<>();

		for (Piece enemyPiece : getEnemyPieces(piece)) {
			if (enemyPiece instanceof King) {
				enemyMoves.addAll(((King) enemyPiece).getStandardMoves());
			} else {
				enemyMoves.addAll(enemyPiece.getValidMoves());
			}
		}

		return enemyMoves;
	}

	public Set<Piece> getAllyPieces(Piece piece) {
		Set<Piece> allyPieces = new HashSet<>();

		for (Piece possibleAlly : pieces) {
			if (!possibleAlly.isEnemyOf(piece.getColor())) {
				allyPieces.add(possibleAlly);
			}
		}

		return allyPieces;
	}

	public Set<? extends Move> getAllyMoves(Piece piece) {
		Set<Move> allyMoves = new HashSet<>();

		for (Piece allyPiece : getAllyPieces(piece)) {
			allyMoves.addAll(allyPiece.getSafeMoves());
		}

		return allyMoves;
	}

	@Nullable
	public King getKing(PieceColor color) {
		for (Piece piece : pieces) {
			if (piece instanceof King king && king.getColor() == color) {
				return king;
			}
		}

		return null;
	}

	@Nullable
	public MateKind checkForMate(PieceColor pieceColor) {
		King king = getKing(pieceColor);

		if (king == null) {
			System.err.println(pieceColor + " King not found in Board.checkForMate()");
			Thread.dumpStack();
			return null;
		}

		if (getAllyMoves(king).isEmpty()) {
			if (king.isChecked()) {
				return MateKind.CHECKMATE;
			}

			return MateKind.STALEMATE;
		}

		return null;
	}

	public boolean isOutOfBounds(int x, int y) {
		return x < getMinX()
				|| x > getMaxX()
				|| y < getMinY()
				|| y > getMaxY();
	}

	public boolean isOutOfBounds(Position position) {
		return isOutOfBounds(position.x(), position.y());
	}

	public int evaluate() {
		return new Evaluation(game, this).evaluate();
	}

	public History getHistory() {
		return history;
	}

	void printBoard() {
		System.out.println(toString());
	}

	public Set<Piece> getPieces() {
		return new HashSet<>(pieces);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int y = getMaxY(); y >= getMinY(); y--) {
			for (int x = getMinX(); x <= getMaxX(); x++) {
				if (board[y][x] != null) {
					sb.append(board[y][x].getSymbol());
				} else {
					sb.append("x");
				}

				sb.append(" ");
			}

			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Clones the board with an empty history and no event listeners.
	 */
	public Board copy() {
		Piece[][] newBoardArray = new Piece[getMaxY() + 1][getMaxX() + 1];
		List<Piece> newPieces = new ArrayList<>();
		Map<Piece, Position> newPositions = new HashMap<>();
		History newHistory = new History();
		List<Consumer<Piece>> newPieceAddedListeners = new ArrayList<>(pieceAddedListeners.size());
		List<SubmittedMoveConsumer> newSubmittedMoveListeners = new ArrayList<>();
		List<PieceMovedConsumer> newPieceMovedListeners = new ArrayList<>();
		List<SubmittedUndoMoveDoneConsumer> newSubmittedUndoMoveDoneListeners = new ArrayList<>();
		List<MoveUndoneConsumer> newMoveUndoneListeners = new ArrayList<>();

		Board newBoard = new Board(
				game,
				newBoardArray,
				newPieces,
				newPositions,
				newHistory,
				newPieceAddedListeners,
				newSubmittedMoveListeners,
				newPieceMovedListeners,
				newSubmittedUndoMoveDoneListeners,
				newMoveUndoneListeners);

		for (Piece piece : pieces) {
			Piece newPiece = piece.copyWith(newBoard);
			newBoard.addPiece(positions.get(piece), newPiece);
		}

		return newBoard;
	}

	@FunctionalInterface
	public interface SubmittedMoveConsumer {
		void accept(Piece piece, Move move);
	}

	@FunctionalInterface
	public interface PieceMovedConsumer {
		void accept(Piece piece, Move move);
	}

	@FunctionalInterface
	public interface SubmittedUndoMoveDoneConsumer {
		void accept(Piece piece, Move move);
	}

	@FunctionalInterface
	public interface MoveUndoneConsumer {
		void accept(Piece piece, Move move);
	}
}
