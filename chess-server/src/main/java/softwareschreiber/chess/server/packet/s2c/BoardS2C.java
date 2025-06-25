package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.BoardS2CData;

public record BoardS2C(BoardS2CData data) implements Packet<BoardS2CData> {
	@Override
	public PacketType type() {
		return PacketType.BoardS2C;
	}
}
