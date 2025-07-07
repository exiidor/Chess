package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.GameInfo;

public record GameS2C(GameInfo data) implements Packet<GameInfo> {
	@Override
	public PacketType type() {
		return PacketType.GameS2C;
	}
}
