package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.RequestMovesC2SData;

public record RequestMovesC2S(PacketType type, RequestMovesC2SData data) implements Packet<RequestMovesC2SData> {
	public RequestMovesC2S(RequestMovesC2SData data) {
		this(PacketType.RequestMovesC2S, data);
	}
}
