package softwareschreiber.chess.server.packet.data.s2c;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.packet.data.component.GameInfo;

public record MoveS2CData(String initiator, Move move) {
}
