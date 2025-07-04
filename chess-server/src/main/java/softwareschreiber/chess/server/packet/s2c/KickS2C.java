package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.KickS2CData;

public record KickS2C(KickS2CData data) implements Packet<KickS2CData> {
	@Override
	public PacketType type() {
		return PacketType.KickS2C;
	}
}
