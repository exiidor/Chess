package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.RequestMovesC2S.Data;

public record RequestMovesC2S(PacketType type, Data data) implements Packet<Data> {
	public RequestMovesC2S(Data data) {
		this(PacketType.RequestMovesC2S, data);
	}

	public record Data(int x, int y) { }
}
