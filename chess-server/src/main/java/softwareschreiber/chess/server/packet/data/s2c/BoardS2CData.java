package softwareschreiber.chess.server.packet.data.s2c;

import softwareschreiber.chess.server.packet.data.component.BoardPojo;

public record BoardS2CData(String gameId, BoardPojo board) {
}
