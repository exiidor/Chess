package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.JoinGameC2S.Data;

public record JoinGameC2S(PacketType type, Data data) implements Packet<Data> {
	public JoinGameC2S(Data data) {
		this(PacketType.InviteResponseC2S, data);
	}

	public record Data(String gameId) { }
}
