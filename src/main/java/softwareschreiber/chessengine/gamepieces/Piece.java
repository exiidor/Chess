package softwareschreiber.chessengine.gamepieces;

import java.util.HashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.move.Move;

public abstract class Piece {
	protected final boolean isWhite;
	protected final Board board;
	private int moveCount;

	public Piece(boolean isWhite, Board board) {
		this.isWhite = isWhite;
		this.board = board;
	}

	public abstract String getName();

	public char getSymbol() {
		return getName().charAt(0);
	}

	public boolean isWhite() {
		return isWhite;
	}

	public boolean isEnemyOf(Piece piece) {
		return isWhite != piece.isWhite();
	}

	public Position getPosition() {
		return board.getPosition(this);
	}

	public int getX() {
		return board.getPosition(this).getX();
	}

	public int getY() {
		return board.getPosition(this).getY();
	}

	public abstract Set<? extends Move> getValidMovesInternal();

	public final Set<? extends Move> getValidMoves(boolean checkCheck) {
		Set<? extends Move> validMoves = getValidMovesInternal();

		if (checkCheck) {
			King king = board.getAllyPieces(this).stream()
					.filter(p -> p instanceof King)
					.map(King.class::cast)
					.findFirst()
					.orElseThrow();

			Set<Move> movesToRemove = new HashSet<>();

			for (Move move : validMoves) {
				board.move(this, move, true);

				if (king.isChecked()) {
					movesToRemove.add(move);
				}

				board.undo();
			}

			validMoves.removeAll(movesToRemove);
		}

		return validMoves;
	}

	public void onMoved(Position oldPosition, Position newPosition) {
		moveCount++;
	}

	public void onMoveUndone(Position oldPosition, Position newPosition) {
		moveCount--;
	}

	public boolean hasMoved() {
		return moveCount > 0;
	}

	protected boolean isAtBoardTop() {
		return getY() == board.getMaxY();
	}

	protected boolean isAtBoardBottom() {
		return getY() == board.getMinY();
	}

	protected boolean isAtBoardLeft() {
		return getX() == board.getMinX();
	}

	protected boolean isAtBoardRight() {
		return getX() == board.getMaxX();
	}

	@Override
	public String toString() {
		return (isWhite ? "White" : "Black") + " " + getName() + " at " + getPosition();
	}
}
