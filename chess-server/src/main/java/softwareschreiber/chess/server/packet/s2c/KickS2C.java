package softwareschreiber.chess.server.packet.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.s2c.KickS2C.Data;

public record KickS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.KickS2C;
	}

	public record Data(String initiator, @Nullable String reason) { }
}
