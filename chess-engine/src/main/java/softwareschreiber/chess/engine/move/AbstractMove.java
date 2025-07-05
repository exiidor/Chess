package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Position;

public abstract class AbstractMove implements Move {
	private final Position source;
	private final Position target;

	public AbstractMove(Position from, Position to) {
		this.source = from;
		this.target = to;
	}

	public AbstractMove(int fromX, int fromY, int toX, int toY) {
		this(new Position(fromX, fromY), new Position(toX, toY));
	}

	@Override
	public Position getSourcePos() {
		return source;
	}

	@Override
	public Position getTargetPos() {
		return target;
	}

	@Override
	public String toString() {
		return source + " -> " + target;
	}
}
