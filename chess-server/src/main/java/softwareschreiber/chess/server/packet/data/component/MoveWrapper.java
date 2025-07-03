package softwareschreiber.chess.server.packet.data.component;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.CastlingMove;
import softwareschreiber.chess.engine.move.EnPassantMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MoveWrapper {
	private MoveType type;
	private Move move;

	public MoveWrapper(Move move) {
		this.move = move;
		this.type = MoveType.of(move);
	}

	private enum MoveType {
		Normal,
		Capture,
		Promotion,
		EnPassant,
		Castling;

		static MoveType of(Move move) {
			if (move instanceof EnPassantMove) {
				return EnPassant;
			} else if (move instanceof PromotionMove) {
				return Promotion;
			} else if (move instanceof CaptureMove) {
				return Capture;
			} else if (move instanceof CastlingMove) {
				return Castling;
			} else {
				return Normal;
			}
		}
	}
}
