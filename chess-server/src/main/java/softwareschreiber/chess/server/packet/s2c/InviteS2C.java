package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.InviteS2CData;

public record InviteS2C(InviteS2CData data) implements Packet<InviteS2CData> {
	@Override
	public PacketType type() {
		return PacketType.InviteS2C;
	}
}
