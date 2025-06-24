package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.CreateGameC2SData;

public record CreateGameC2S(PacketType type, CreateGameC2SData data) implements Packet<CreateGameC2SData> {
	public CreateGameC2S(CreateGameC2SData data) {
		this(PacketType.CreateGameC2S, data);
	}
}
