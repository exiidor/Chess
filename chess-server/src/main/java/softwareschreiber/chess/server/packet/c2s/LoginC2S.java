package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.LoginC2SData;

public record LoginC2S(PacketType type, LoginC2SData data) implements Packet<LoginC2SData> {
	public LoginC2S(LoginC2SData data) {
		this(PacketType.LoginC2S, data);
	}
}
