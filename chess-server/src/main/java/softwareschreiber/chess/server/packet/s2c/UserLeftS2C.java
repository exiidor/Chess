package softwareschreiber.chess.server.packet.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.UserLeftS2C.Data;

public record UserLeftS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.UserLeftS2C;
	}

	public record Data(UserInfo user, @Nullable String reason) { }
}
