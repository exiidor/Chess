package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.KickS2CData;

public record KickS2C(PacketType type, KickS2CData data) implements Packet<KickS2CData> {
}
