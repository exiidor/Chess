package softwareschreiber.chess.server.packet;

import java.util.HashMap;
import java.util.Map;

import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S;
import softwareschreiber.chess.server.packet.c2s.LeaveGameC2S;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.c2s.MoveC2S;
import softwareschreiber.chess.server.packet.c2s.RequestMovesC2S;
import softwareschreiber.chess.server.packet.c2s.SpectateGameC2S;
import softwareschreiber.chess.server.packet.s2c.BoardS2C;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C;
import softwareschreiber.chess.server.packet.s2c.GameEndedS2C;
import softwareschreiber.chess.server.packet.s2c.GameS2C;
import softwareschreiber.chess.server.packet.s2c.GamesS2C;
import softwareschreiber.chess.server.packet.s2c.InviteS2C;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.MoveS2C;
import softwareschreiber.chess.server.packet.s2c.MovesS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;
import softwareschreiber.chess.server.packet.s2c.UserLeftS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public enum PacketType {
	LoginC2S(LoginC2S.class),
	LoginResultS2C(LoginResultS2C.class),
	UserListS2C(UserListS2C.class),
	GamesS2C(GamesS2C.class),
	KickS2C(KickS2C.class),
	CreateGameC2S(CreateGameC2S.class),
	CreateGameResultS2C(CreateGameResultS2C.class),
	InviteS2C(InviteS2C.class),
	InviteResponseC2S(InviteResponseC2S.class),
	UserJoinedS2C(UserJoinedS2C.class),
	JoinGameS2C(JoinGameS2C.class),
	BoardS2C(BoardS2C.class),
	LeaveGameC2S(LeaveGameC2S.class),
	UserLeftS2C(UserLeftS2C.class),
	RequestMovesC2S(RequestMovesC2S.class),
	MovesS2C(MovesS2C.class),
	MoveC2S(MoveC2S.class),
	MoveS2C(MoveS2C.class),
	GameS2C(GameS2C.class),
	SpectateGameC2S(SpectateGameC2S.class),
	GameEndedS2C(GameEndedS2C.class);

	private static final Map<String, PacketType> byJsonName = new HashMap<>();
	private final Class<? extends Packet<?>> packetClass;

	static {
		for (PacketType packetType : values()) {
			byJsonName.put(packetType.name(), packetType);
		}
	}

	PacketType(Class<? extends Packet<?>> packetClass) {
		this.packetClass = packetClass;
	}

	public static PacketType fromJsonName(String jsonName) {
		PacketType packetType = byJsonName.get(jsonName);

		if (packetType == null) {
			throw new IllegalArgumentException("Unknown packet type: " + jsonName);
		}

		return packetType;
	}

	public String jsonName() {
		return name();
	}

	@Override
	public String toString() {
		return name();
	}

	public Class<? extends Packet<?>> packetClass() {
		return packetClass;
	}
}
