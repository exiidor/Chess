package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C.Data;

public record JoinGameS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.JoinGameS2C;
	}

	public record Data(String gameId) { }
}
