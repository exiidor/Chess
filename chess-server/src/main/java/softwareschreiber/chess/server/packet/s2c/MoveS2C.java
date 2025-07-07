package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.s2c.MoveS2C.Data;

public record MoveS2C(Data data) implements Packet<Data> {
	@Override
	public PacketType type() {
		return PacketType.MoveS2C;
	}

	public record Data(PieceColor color, Move move) { }
}
