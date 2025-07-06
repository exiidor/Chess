package softwareschreiber.chess.server.packet.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C.Data;

public record CreateGameResultS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.CreateGameResultS2C;
	}

	public record Data(@Nullable String gameId, @Nullable String error) { }
}
