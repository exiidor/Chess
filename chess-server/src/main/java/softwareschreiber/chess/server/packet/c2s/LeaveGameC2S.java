package softwareschreiber.chess.server.packet.c2s;

import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.data.c2s.LeaveGameC2SData;

public record LeaveGameC2S(PacketType type, LeaveGameC2SData data) implements Packet<LeaveGameC2SData> {
	public LeaveGameC2S(LeaveGameC2SData data) {
		this(PacketType.LeaveGameC2S, data);
	}
}
