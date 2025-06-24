package softwareschreiber.chess.server.packet.data.s2c;

import softwareschreiber.chess.server.packet.data.component.GameInfo;

public record InviteS2CData(String initiator, GameInfo gameInfo) {
}
