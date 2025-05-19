package softwareschreiber.chess.server;

public enum PacketType {
	LoginC2S,
	LoginResultS2C,
	UserListS2C;

	private final String jsonName;

	PacketType() {
		this.jsonName = name();
	}

	public String jsonName() {
		return jsonName;
	}

	@Override
	public String toString() {
		return jsonName;
	}
}
