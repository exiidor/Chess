package softwareschreiber.chess.server;

import java.util.HashMap;
import java.util.Map;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.s2c.InviteS2C;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public enum PacketType {
	LoginC2S(LoginC2S.class),
	LoginResultS2C(LoginResultS2C.class),
	UserListS2C(UserListS2C.class),
	KickS2C(KickS2C.class),
	CreateGameC2S(CreateGameC2S.class),
	CreateGameResultS2C(CreateGameResultS2C.class),
	InviteS2C(InviteS2C.class),
	InviteResponseC2S(InviteResponseC2S.class),
	UserJoinedS2C(UserJoinedS2C.class),
	JoinGameS2C(JoinGameS2C.class);

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
