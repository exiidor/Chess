package softwareschreiber.chess.server.packet.data.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.gamepieces.Piece;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BoardPojo {
	private final int maxX;
	private final int maxY;
	private PiecePojo[] pieces;

	public BoardPojo(Board board) {
		maxX = board.getMaxX();
		maxY = board.getMaxY();

		pieces = new PiecePojo[(maxX + 1) * (maxY + 1)];

		for (int x = 0; x <= maxX; x++) {
			for (int y = 0; y <= maxY; y++) {
				Piece piece = board.getPieceAt(x, y);

				if (piece != null) {
					pieces[x + y * (maxX + 1)] = new PiecePojo(piece);
				}
			}
		}
	}
}
