package softwareschreiber.chess.engine.move;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;

public class NormalMove extends AbstractMove {
	public NormalMove(Position from, Position to) {
		super(from, to);
	}

	public NormalMove(int fromX, int fromY, int toX, int toY) {
		this(new Position(fromX, fromY), new Position(toX, toY));
	}

	public Move copyWith(Board board) {
		return this;
	}
}
