package softwareschreiber.chess.engine.move;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import softwareschreiber.chess.engine.Board;
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
	public Position sourcePos() {
		return source;
	}

	@Override
	public Position targetPos() {
		return target;
	}

	@Override
	public String toString() {
		return source + " -> " + target;
	}
}
