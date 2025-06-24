package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.InviteResponseC2SData;

public record InviteResponseC2S(PacketType type, InviteResponseC2SData data) implements Packet<InviteResponseC2SData> {
	public InviteResponseC2S(InviteResponseC2SData data) {
		this(PacketType.InviteResponseC2S, data);
	}
}
