package softwareschreiber.chess.server;

import java.util.HashMap;
import java.util.Map;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public enum PacketType {
	LoginC2S(LoginC2S.class),
	LoginResultS2C(LoginResultS2C.class),
	UserListS2C(UserListS2C.class),
	KickS2C(KickS2C.class);

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
