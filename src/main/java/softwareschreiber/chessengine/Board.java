package softwareschreiber.chessengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.CastlingMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.move.PromotionMove;
import softwareschreiber.chessengine.util.History;
import softwareschreiber.chessengine.util.Pair;

public class Board {
	private Piece[][] board;
	private List<Piece> pieces;
	private Map<Piece, Position> positions;
	private History<Pair<Piece, Move>> history;

	public Board() {
		board = new Piece[8][8];
		pieces = new ArrayList<>();
		positions = new HashMap<>();
		history = new History<>(Pair.of(null, null));
	}

	public void initializeStartingPositions() {
		for (int i = 0; i <= 1; i++) {
			boolean isWhite = i == 0;
			int y = isWhite ? 0 : 7;

			addPiece(0, y, new Rook(isWhite, this));
			addPiece(1, y, new Knight(isWhite, this));
			addPiece(2, y, new Bishop(isWhite, this));
			addPiece(3, y, new Queen(isWhite, this));
			addPiece(4, y, new King(isWhite, this));
			addPiece(5, y, new Bishop(isWhite, this));
			addPiece(6, y, new Knight(isWhite, this));
			addPiece(7, y, new Rook(isWhite, this));
			y = isWhite ? 1 : 6;

			for (int x = 0; x < 8; x++) {
				addPiece(x, y, new Pawn(isWhite, this));
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
		return piece;
	}

	public void move(Piece piece, Move move) {
		Position currentPosition = positions.get(piece);
		Position targetPosition = move.getTargetPos();

		// Special moves
		if (move instanceof CastlingMove castlingMove) {
			move(castlingMove.getOther(), castlingMove.getOtherMove());
		} else if (move instanceof PromotionMove promotionMove) {
			if (promotionMove.getCaptured() != null) {
				capture(promotionMove.getCaptured());
			}

			Pawn pawn = (Pawn) piece;
			piece = getPromotionTarget(pawn);
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
		history.push(Pair.of(piece, move));
	}

	private void capture(Piece piece) {
		Position position = positions.get(piece);
		pieces.remove(piece);
		board[position.getY()][position.getX()] = null;
	}

	protected Piece getPromotionTarget(Pawn pawn) {
		String choice = null;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Choose piece to promote to: (Queen, Rook, Bishop, Knight)");
			choice = scanner.nextLine();
		}

		switch (choice) {
			case "Queen":
				return new Queen(pawn.isWhite(), this);
			case "Rook":
				return new Rook(pawn.isWhite(), this);
			case "Bishop":
				return new Bishop(pawn.isWhite(), this);
			case "Knight":
				return new Knight(pawn.isWhite(), this);
			default:
				System.out.println("Invalid choice");
				return getPromotionTarget(pawn);
		}
	}

	public void undo() {
		Pair<Piece, Move> lastMove = history.getCurrent();

		if (lastMove == null) {
			return;
		}

		undoMove(lastMove.getLeft(), lastMove.getRight());
	}

	private void undoMove(Piece piece, Move move) {
		Position currentPosition = move.getTargetPos();
		Position targetPosition = move.getSourcePos();

		board[currentPosition.getY()][currentPosition.getX()] = null;
		board[targetPosition.getY()][targetPosition.getX()] = piece;
		positions.put(piece, targetPosition);

		if (move instanceof CastlingMove castlingMove) {
			undoMove(castlingMove.getOther(), castlingMove.getOtherMove());
		} else if (move instanceof PromotionMove promotionMove) {
			Piece replacement = promotionMove.getReplacement();
			Pawn originalPawn = (Pawn) piece;
			pieces.remove(replacement);
			positions.remove(replacement);
			board[currentPosition.getY()][currentPosition.getX()] = null;
			addPiece(targetPosition, originalPawn);

			if (promotionMove.getCaptured() != null) {
				undoMove(piece, new CaptureMove(promotionMove.getSourcePos(), promotionMove.getTargetPos(),
						promotionMove.getCaptured()));
			}
		} else if (move instanceof CaptureMove captureMove) {
			Position capturedPos = captureMove.getCaptured().getPosition();
			addPiece(capturedPos, captureMove.getCaptured());
			undoMove(captureMove.getCaptured(), new Move(capturedPos, capturedPos));
		}
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
			enemyMoves.addAll(enemyPiece.getValidMoves(false));
		}

		return enemyMoves;
	}

	public Set<Piece> getAllyPieces(Piece piece) {
		Set<Piece> allyPieces = new HashSet<>();

		for (Piece possibleAlly : pieces) {
			if (!possibleAlly.isEnemyOf(piece)) {
				allyPieces.add(possibleAlly);
			}
		}

		return allyPieces;
	}

	public Set<? extends Move> getAllAllyMoves(Piece piece) {
		Set<Move> allyMoves = new HashSet<>();

		for (Piece allyPiece : getAllyPieces(piece)) {
			allyMoves.addAll(allyPiece.getValidMoves(true));
		}

		return allyMoves;
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

	public History<Pair<Piece, Move>> getHistory() {
		return history;
	}

	void printBoard() {
		System.out.println(toString());
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
}
