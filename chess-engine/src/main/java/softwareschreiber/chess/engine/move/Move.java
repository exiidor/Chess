package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;

/**
 * Represents a move in the chess game.
 * A move consists of a source position and a target position.
 */
public class Move {
	private final Position source;
	private final Position target;

	/**
	 * Constructs a move with the specified source and target positions.
	 *
	 * @param from the source position
	 * @param to   the target position
	 */
	public Move(Position from, Position to) {
		this.source = from;
		this.target = to;
	}

	/**
	 * Constructs a move with the positions specified by their x and y coordinates.
	 *
	 * @param fromX the x-coordinate of the source position
	 * @param fromY the y-coordinate of the source position
	 * @param toX   the x-coordinate of the target position
	 * @param toY   the y-coordinate of the target position
	 */
	public Move(int fromX, int fromY, int toX, int toY) {
		this(new Position(fromX, fromY), new Position(toX, toY));
	}

	public Position getSourcePos() {
		return source;
	}

	public Position getTargetPos() {
		return target;
	}

	@Override
	public String toString() {
		return source + " -> " + target;
	}

	public Move copyWith(Board board) {
		return this;
	}
}
