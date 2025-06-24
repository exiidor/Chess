package softwareschreiber.chess.server.packet.s2c;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.s2c.JoinGameS2CData;

public record JoinGameS2C(JoinGameS2CData data) implements Packet<JoinGameS2CData> {
	@Override
	public PacketType type() {
		return PacketType.JoinGameS2C;
	}
}
