package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.component.UserInfo;

public record UserJoinedS2C(UserInfo data) implements Packet<UserInfo> {
	@Override
	public PacketType type() {
		return PacketType.UserJoinedS2C;
	}
}
