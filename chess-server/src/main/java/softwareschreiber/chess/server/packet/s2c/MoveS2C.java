package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.MoveS2CData;

public record MoveS2C(MoveS2CData data) implements Packet<MoveS2CData> {
	@Override
	public PacketType type() {
		return PacketType.MoveS2C;
	}
}
