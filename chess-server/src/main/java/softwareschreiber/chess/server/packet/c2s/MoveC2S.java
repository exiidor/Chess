package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.MoveC2S.Data;

public record MoveC2S(PacketType type, Data data) implements Packet<Data> {
	public MoveC2S(Data data) {
		this(PacketType.MoveC2S, data);
	}

	public record Data(int committedMoveIndex) { }
}
