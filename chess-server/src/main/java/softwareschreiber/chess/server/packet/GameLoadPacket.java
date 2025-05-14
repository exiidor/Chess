package softwareschreiber.chess.server.packet;

public record GameLoadPacket(String[][] board, String currentPlayer) {
}
