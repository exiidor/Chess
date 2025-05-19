package softwareschreiber.chess.server.packet.data.c2s;

public record LoginC2SData(String username, String password, String clientVersion) {
}
