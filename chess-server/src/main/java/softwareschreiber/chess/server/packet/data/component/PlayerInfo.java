package softwareschreiber.chess.server.packet.data.component;

public record PlayerInfo(
		String username,
		String status,
		boolean playing,
		int gamesPlayed,
		int gamesWon,
		int gamesLost,
		int gamesDrawn) {
}
