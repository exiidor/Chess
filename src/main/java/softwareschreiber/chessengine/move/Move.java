package softwareschreiber.chessengine.move;

import softwareschreiber.chessengine.Position;

public class Move {
	private Position source;
	private Position target;

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
}
