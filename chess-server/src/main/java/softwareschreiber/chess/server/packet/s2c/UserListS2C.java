package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.UserInfo;

public record UserListS2C(Collection<UserInfo> data) implements Packet<Collection<UserInfo>> {
	@Override
	public PacketType type() {
		return PacketType.UserListS2C;
	}
}
