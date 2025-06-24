package softwareschreiber.chess.server.packet.data.c2s;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.gamepieces.PieceColor;

public record CreateGameC2SData(boolean cpuOpponent, @Nullable String requestedOpponent, PieceColor ownColor, int maxSecondsPerMove) {
}
