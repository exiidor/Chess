package softwareschreiber.chessengine;

import java.util.Set;

public abstract class Piece {
	private final boolean isWhite;
	protected final Board board;

	public Piece(boolean isWhite, Board board) {
		this.isWhite = isWhite;
		this.board = board;
	}

	public abstract char getSymbol();

	public boolean isWhite() {
		return isWhite;
	}

	public boolean isEnemyOf(Piece piece) {
		return isWhite != piece.isWhite();
	}

	public int getX() {
		return board.getPosition(this).getX();
	}

	public int getY() {
		return board.getPosition(this).getY();
	}

	public abstract Set<Position> getValidMoves();

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
}
