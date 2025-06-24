package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.LoginResultS2CData;

public record LoginResultS2C(LoginResultS2CData data) implements Packet<LoginResultS2CData> {
	@Override
	public PacketType type() {
		return PacketType.LoginResultS2C;
	}
}
