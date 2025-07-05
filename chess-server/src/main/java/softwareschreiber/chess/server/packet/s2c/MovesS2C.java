package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;

public record MovesS2C(Collection<? extends Move> data) implements Packet<Collection<? extends Move>> {
	@Override
	public PacketType type() {
		return PacketType.MovesS2C;
	}
}
