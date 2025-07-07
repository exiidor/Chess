package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.s2c.InviteS2C.Data;

public record InviteS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.InviteS2C;
	}

	public record Data(String initiator, GameInfo gameInfo) { }
}
