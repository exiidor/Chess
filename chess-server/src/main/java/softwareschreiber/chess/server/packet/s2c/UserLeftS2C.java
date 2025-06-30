package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.UserLeftS2CData;

public record UserLeftS2C(UserLeftS2CData data) implements Packet<UserLeftS2CData> {
	@Override
	public PacketType type() {
		return PacketType.UserLeftS2C;
	}
}
