package softwareschreiber.chessengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import softwareschreiber.chessengine.evaluation.Evaluation;
import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.CastlingMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.util.History;
import softwareschreiber.chessengine.util.Pair;

public class Board {
	private final Game game;
	private final Piece[][] board;
	private final List<Piece> pieces;
	private final Map<Piece, Position> positions;
	private final History<Pair<Piece, Move>> history;
	private final List<Consumer<Piece>> pieceAddedListeners;
	private final List<SubmittedMoveConsumer> submittedMoveListeners;
	private final List<PieceMovedConsumer> pieceMovedListeners;
	private final List<SubmittedUndoMoveDoneConsumer> submittedUndoMoveDoneListeners;
	private final List<MoveUndoneConsumer> moveUndoneListeners;

	public Board(Game game) {
		this(game,
				new Piece[8][8],
				new ArrayList<>(),
				new HashMap<>(),
				new History<>(Pair.of(null, null)),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>());
	}

	private Board(
			Game game,
			Piece[][] board,
			List<Piece> pieces,
			Map<Piece, Position> positions,
			History<Pair<Piece, Move>> history,
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

	public void initializeStartingPositions() {
		for (int i = 0; i <= 1; i++) {
			boolean isWhite = i == 0;
			int y = isWhite ? 0 : 7;
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
		return addPiece(position.getX(), position.getY(), piece);
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
	public void move(Piece piece, Move move, boolean virtual) {
		moveInternal(piece, move, virtual);

		if (!virtual) {
			submittedMoveListeners.forEach(listener -> listener.accept(piece, move));
		}
	}

	private void moveInternal(Piece piece, Move move, boolean virtual) {
		Piece origPiece = piece;
		Position currentPosition = positions.get(piece);
		Position targetPosition = move.getTargetPos();

		if (!currentPosition.equals(move.getSourcePos())) {
			throw new IllegalArgumentException("Piece is not at source position");
		}

		// Special moves
		if (move instanceof CastlingMove castlingMove) {
			moveInternal(castlingMove.getOther(), castlingMove.getOtherMove(), virtual);
		} else if (move instanceof PromotionMove promotionMove) {
			if (promotionMove.getCaptured() != null) {
				capture(promotionMove.getCaptured());
			}

			Pawn pawn = (Pawn) piece;

			if (!virtual) {
				piece = game.getPromotionTarget(this, pawn);
			} else {
				// Default to queen
				piece = new Queen(piece.getColor(), this);
			}

			promotionMove.setReplacement(piece);
			pieces.remove(pawn);
			pieces.add(piece);
		} else if (move instanceof CaptureMove captureMove) {
			capture(captureMove.getCaptured());
		}

		// Throw error if target position is not empty
		assert getPieceAt(targetPosition) == null;

		// Update position
		board[currentPosition.getY()][currentPosition.getX()] = null;
		board[targetPosition.getY()][targetPosition.getX()] = piece;
		positions.put(piece, targetPosition);
		piece.onMoved(currentPosition, targetPosition);

		// History
		history.push(Pair.of(origPiece, move));

		pieceMovedListeners.forEach(listener -> listener.accept(origPiece, move));
	}

	private void capture(Piece piece) {
		Position position = positions.get(piece);
		pieces.remove(piece);
		board[position.getY()][position.getX()] = null;
	}

	public void undo(boolean virtual) {
		Pair<Piece, Move> lastMove = history.getCurrent();

		if (lastMove == null) {
			return;
		}

		if (lastMove.getLeft() == null || lastMove.getRight() == null) {
			throw new IllegalStateException("Last move is invalid");
		}

		undoMove(lastMove.getLeft(), lastMove.getRight(), virtual);
	}

	private void undoMove(Piece piece, Move move, boolean virtual) {
		undoMoveInternal(piece, move, virtual, true);

		if (!virtual) {
			submittedUndoMoveDoneListeners.forEach(listener -> listener.accept(piece, move));
		}
	}

	public void undoMoveInternal(Piece piece, Move move, boolean virtual, boolean modifyHistory) {
		Position currentPosition = move.getTargetPos();
		Position targetPosition = move.getSourcePos();

		board[currentPosition.getY()][currentPosition.getX()] = null;
		board[targetPosition.getY()][targetPosition.getX()] = piece;
		positions.put(piece, targetPosition);

		if (move instanceof CastlingMove castlingMove) {
			undoMoveInternal(castlingMove.getOther(), castlingMove.getOtherMove(), virtual, true);
		} else if (move instanceof PromotionMove promotionMove) {
			Piece replacement = promotionMove.getReplacement();
			Pawn originalPawn = (Pawn) piece;
			pieces.remove(replacement);
			positions.remove(replacement);
			board[currentPosition.getY()][currentPosition.getX()] = null;
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
			undoMoveInternal(captureMove.getCaptured(), new Move(capturedPos, capturedPos), virtual, false);
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

	public int getMinX() {
		return 0;
	}

	public int getMinY() {
		return 0;
	}

	public int getMaxX() {
		return 7;
	}

	public int getMaxY() {
		return 7;
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
		return getPieceAt(position.getX(), position.getY());
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

	public Set<? extends Move> getAllEnemyMoves(Piece piece) {
		Set<Move> enemyMoves = new HashSet<>();

		for (Piece enemyPiece : getEnemyPieces(piece)) {
			enemyMoves.addAll(enemyPiece.getValidMoves());
		}

		return enemyMoves;
	}

	public Set<? extends Move> getAllEnemyMovesExceptKingMoves(Piece piece) {
		Set<Move> enemyMoves = new HashSet<>();

		for (Piece enemyPiece : getEnemyPieces(piece)) {
			if (!(enemyPiece instanceof King)) {
				enemyMoves.addAll(enemyPiece.getValidMovesInternal());
			} else if (enemyPiece instanceof King) {
				enemyMoves.addAll(((King) enemyPiece).getNormalKingMoves());
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

	public Set<? extends Move> getAllMoves() {
		Set<Move> allMoves = new HashSet<>();

		for (Piece piece : pieces) {
			allMoves.addAll(piece.getValidMoves());
		}

		return allMoves;
	}

	public Set<? extends Move> getAllAllyMoves(Piece piece) {
		Set<Move> allyMoves = new HashSet<>();

		for (Piece allyPiece : getAllyPieces(piece)) {
			allyMoves.addAll(allyPiece.getValidMovesInternal());
		}

		return allyMoves;
	}

	public void checkForEnemyMates(Piece piece) {
		Set<? extends Move> enemyMoves = getAllEnemyMoves(piece);
		String color = piece.getEnemyColor().toString();

		if (enemyMoves.isEmpty()) {
			for (Piece enemyPiece : getEnemyPieces(piece)) {
				if (enemyPiece instanceof King king && king.isChecked()) {
					game.checkMate(color);
					return;
				}
			}

			game.staleMate();
		}
	}

	public boolean isOutOfBounds(int x, int y) {
		return x < getMinX()
				|| x > getMaxX()
				|| y < getMinY()
				|| y > getMaxY();
	}

	public boolean isOutOfBounds(Position position) {
		return isOutOfBounds(position.getX(), position.getY());
	}

	public int evaluate() {
		return new Evaluation(this).evaluate();
	}

	public History<Pair<Piece, Move>> getHistory() {
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

		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
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
		Piece[][] newBoardArray = new Piece[8][8];
		List<Piece> newPieces = new ArrayList<>();
		Map<Piece, Position> newPositions = new HashMap<>();
		History<Pair<Piece, Move>> newHistory = new History<Pair<Piece, Move>>(Pair.of(null, null));
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
