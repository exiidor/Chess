package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.component.PlayerInfo;

public record UserListS2C(PacketType type, Collection<PlayerInfo> data) implements Packet<Collection<PlayerInfo>> {
}
