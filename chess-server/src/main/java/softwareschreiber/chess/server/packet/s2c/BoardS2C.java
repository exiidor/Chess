package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.BoardPojo;
import softwareschreiber.chess.server.packet.s2c.BoardS2C.Data;

public record BoardS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.BoardS2C;
	}

	public record Data(String gameId, BoardPojo board) { }
}
