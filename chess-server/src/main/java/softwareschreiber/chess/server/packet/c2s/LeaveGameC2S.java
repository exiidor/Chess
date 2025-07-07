package softwareschreiber.chess.server.packet.c2s;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.LeaveGameC2S.Data;

public record LeaveGameC2S(PacketType type, Data data) implements Packet<Data> {
	public LeaveGameC2S(Data data) {
		this(PacketType.LeaveGameC2S, data);
	}

	public record Data(@Nullable String reason) { }
}
