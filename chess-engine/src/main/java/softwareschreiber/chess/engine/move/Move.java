package softwareschreiber.chess.engine.move;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Position;

/**
 * Represents a move in a chess game.
 */
@JsonTypeInfo(use = Id.SIMPLE_NAME, include = As.PROPERTY, property = "type") // Include Java class simple-name as JSON property "type"
public interface Move {
	Position getSourcePos();
	Position getTargetPos();
	Move copyWith(Board board);
}
