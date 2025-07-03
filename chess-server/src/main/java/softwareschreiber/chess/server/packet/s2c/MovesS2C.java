package softwareschreiber.chess.server.packet.s2c;

import java.util.Collection;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.component.MoveWrapper;
import softwareschreiber.chess.server.packet.data.component.UserInfo;

public record MovesS2C(Collection<MoveWrapper> data) implements Packet<Collection<MoveWrapper>> {
	public static MovesS2C of(Collection<? extends Move> moves) {
		return new MovesS2C(moves.stream().map(MoveWrapper::new).toList());
	}

	@Override
	public PacketType type() {
		return PacketType.MovesS2C;
	}
}
