package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.SpectateGameC2S.Data;

public record SpectateGameC2S(PacketType type, Data data) implements Packet<Data> {
	public SpectateGameC2S(Data data) {
		this(PacketType.SpectateGameC2S, data);
	}

	public record Data(String gameId) { }
}
