package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S.Data;

public record InviteResponseC2S(PacketType type, Data data) implements Packet<Data> {
	public InviteResponseC2S(Data data) {
		this(PacketType.InviteResponseC2S, data);
	}

	public record Data(boolean accept, String gameId) { }
}
