package softwareschreiber.chess.engine.gamepieces;

import java.util.HashSet;
import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.move.Move;

public abstract class Piece {
	protected final PieceColor color;
	protected final Board board;
	private int moveCount;

	public Piece(PieceColor color, Board board) {
		this.color = color;
		this.board = board;
	}

	public abstract String getName();

	public abstract char getSymbol();

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
		return board.getPosition(this).x();
	}

	public int getY() {
		return board.getPosition(this).y();
	}

	public abstract int getValue();

	public abstract int[][] getEvaluationChart();

	/**
	 * Returns the valid moves of the piece, even the ones that would put the king in check.
	 */
	public abstract Set<? extends Move> getValidMoves();

	/**
	 * Returns the valid moves of the piece, excluding the ones that would put the king in check.
	 */
	public final Set<? extends Move> getSafeMoves() {
		Set<? extends Move> validMoves = getValidMoves();
		King king = this instanceof King thisKing ? thisKing : board.getKing(color);

		if (king == null) {
			System.err.println(color + " King not found in Piece.getValidMoves");
			Thread.dumpStack();
		} else {
			Set<Move> movesToRemove = new HashSet<>();

			for (Move move : validMoves) {
				board.move(this, move, board.getGame().getSimulationPlayer(color));

				if (king.isChecked()) {
					movesToRemove.add(move);
				}

				board.undo(true);
			}

			validMoves.removeAll(movesToRemove);
		}

		return validMoves;
	}

	public boolean isUnderAttack() {
		Set<? extends Move> enemyMoves = this instanceof King king
				? board.getEnemyMovesExceptKingMoves(king)
				: board.getEnemyMoves(this);

		return enemyMoves.stream()
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

	/**
	 * Returns the theoretical maximum number of moves this piece can make.
	 */
	// TODO: Adjust for dynamic board sizes
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
