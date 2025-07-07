package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;

public record MovesS2C(Collection<? extends Move> data) implements Packet<Collection<? extends Move>> {
	@Override
	public PacketType type() {
		return PacketType.MovesS2C;
	}
}
