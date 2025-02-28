package softwareschreiber.chessengine.gamepieces;

import java.util.HashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.move.Move;

public abstract class Piece {
	protected final PieceColor color;
	protected final Board board;
	private int moveCount;

	public Piece(PieceColor color, Board board) {
		this.color = color;
		this.board = board;
	}

	public abstract String getName();

	public char getSymbol() {
		return getName().charAt(0);
	}

	public PieceColor getColor() {
		return color;
	}

	public PieceColor getEnemyColor() {
		return color.getOpposite();
	}

	public boolean isWhite() {
		return color == PieceColor.WHITE;
	}

	public boolean isBlack() {
		return color == PieceColor.BLACK;
	}

	public boolean isEnemyOf(Piece piece) {
		return color != piece.getColor();
	}

	public boolean isEnemyOf(PieceColor color) {
		return this.color != color;
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

	public abstract int getValue();

	public abstract int[][] evaluationChart();

	public abstract Set<? extends Move> getValidMovesInternal();

	public final Set<? extends Move> getValidMoves() {
		Set<? extends Move> validMoves = getValidMovesInternal();
		King king = board.getKing(color);

		if (king == null) {
			System.err.println(color + " King not found in Piece.getValidMoves");
			Thread.dumpStack();
		} else {
			Set<Move> movesToRemove = new HashSet<>();

			for (Move move : validMoves) {
				board.move(this, move, true);

				if (king.isChecked()) {
					movesToRemove.add(move);
				}

				board.undo(true);
			}

			validMoves.removeAll(movesToRemove);
		}

		return validMoves;
	}

	public boolean isUnderAttack(){
		return board.getAllEnemyMovesExceptKingMoves(this).stream()
				.map(Move::getTargetPos)
				.anyMatch(pos -> pos.equals(getPosition()));
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

	public abstract int getMaxMoves();

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
		return color + " " + getName() + " at " + getPosition();
	}

	/**
	 * Clones the piece and sets the board to the given board.
	 */
	public abstract Piece copyWith(Board board);
}
