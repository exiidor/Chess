package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.component.UserInfo;

public record UserListS2C(PacketType type, Collection<UserInfo> data) implements Packet<Collection<UserInfo>> {
}
