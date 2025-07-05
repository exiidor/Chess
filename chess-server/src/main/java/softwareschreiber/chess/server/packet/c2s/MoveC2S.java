package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.MoveC2SData;

public record MoveC2S(PacketType type, MoveC2SData data) implements Packet<MoveC2SData> {
	public MoveC2S(MoveC2SData data) {
		this(PacketType.MoveC2S, data);
	}
}
