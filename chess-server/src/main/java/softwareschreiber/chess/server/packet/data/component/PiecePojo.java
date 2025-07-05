package softwareschreiber.chess.server.packet.data.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PiecePojo {
	private String type;
	private PieceColor color;
	private char symbol;
	private int x;
	private int y;

	public PiecePojo(Piece piece) {
		this.type = piece.getName();
		this.color = piece.getColor();
		this.symbol = piece.getSymbol();

		if (piece.getPosition() != null) {
			this.x = piece.getX();
			this.y = piece.getY();
		} else { // unplaced promotion move piece
			this.x = -1;
			this.y = -1;
		}
	}
}
