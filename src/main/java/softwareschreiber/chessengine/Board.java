package softwareschreiber.chessengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.King;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;

public class Board {
	private Piece[][] board;
	private List<Piece> pieces;
	private Map<Piece, Position> positions;

	Board() {
		board = new Piece[8][8];
		pieces = new ArrayList<>();
		positions = new HashMap<>();
	}

	void initializeStartingPosition() {
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

	Piece addPiece(int x, int y, Piece piece) {
		pieces.add(piece);
		positions.put(piece, new Position(x, y));
		board[y][x] = piece;
		return piece;
	}

	public void move(Piece piece, Position targetPosition) {
		Position currentPosition = positions.get(piece);
		board[currentPosition.getX()][currentPosition.getY()] = null;
		board[targetPosition.getX()][targetPosition.getY()] = piece;
		positions.put(piece, targetPosition);
		piece.onMoved(currentPosition, targetPosition);
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
			enemyMoves.addAll(enemyPiece.getValidMoves());
		}

		return enemyMoves;
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
