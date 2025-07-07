package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.GameInfo;

public record GamesS2C(Collection<GameInfo> data) implements Packet<Collection<GameInfo>> {
	@Override
	public PacketType type() {
		return PacketType.GamesS2C;
	}
}
