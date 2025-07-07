package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.LoginC2S.Data;

public record LoginC2S(PacketType type, Data data) implements Packet<Data> {
	public LoginC2S(Data data) {
		this(PacketType.LoginC2S, data);
	}

	public record Data(String username, String password, String clientVersion) { }
}
