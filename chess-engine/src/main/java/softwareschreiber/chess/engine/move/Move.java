package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;

public class Move {
	private final Position source;
	private final Position target;

	public Move(Position from, Position to) {
		this.source = from;
		this.target = to;
	}

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
