package softwareschreiber.chess.server;

public enum Commands {
	NEW_GAME;

	public String asText() {
		return name().toLowerCase().replace("_", "-");
	}
}
