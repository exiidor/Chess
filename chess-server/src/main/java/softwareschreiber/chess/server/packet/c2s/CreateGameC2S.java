package softwareschreiber.chess.server.packet.c2s;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S.Data;

public record CreateGameC2S(PacketType type, Data data) implements Packet<Data> {
	public CreateGameC2S(Data data) {
		this(PacketType.CreateGameC2S, data);
	}

	public record Data(boolean cpuOpponent, @Nullable String requestedOpponent, PieceColor ownColor, int maxSecondsPerMove) { }
}
