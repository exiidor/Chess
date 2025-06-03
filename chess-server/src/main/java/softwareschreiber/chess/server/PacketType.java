package softwareschreiber.chess.server;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {
	LoginC2S,
	LoginResultS2C,
	UserListS2C,
	KickS2C;

	private static final Map<String, PacketType> byJsonName = new HashMap<>();

	static {
		for (PacketType packetType : values()) {
			byJsonName.put(packetType.name(), packetType);
		}
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
}
