package softwareschreiber.chess.server.packet.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C.Data;

public record LoginResultS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.LoginResultS2C;
	}

	public record Data(@Nullable String error) { }
}
