package softwareschreiber.chess.engine.move;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

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
