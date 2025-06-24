package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.CreateGameResultS2CData;

public record CreateGameResultS2C(CreateGameResultS2CData data) implements Packet<CreateGameResultS2CData> {
	@Override
	public PacketType type() {
		return PacketType.CreateGameResultS2C;
	}
}
