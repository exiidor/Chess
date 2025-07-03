package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.LoginC2SData;

public record MoveC2S(PacketType type, Move data) implements Packet<Move> {
	public MoveC2S(Move data) {
		this(PacketType.MoveC2S, data);
	}
}
