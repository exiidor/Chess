package softwareschreiber.chess.server.packet.data.s2c;

import org.jetbrains.annotations.Nullable;

public record CreateGameResultS2CData(@Nullable String gameId, @Nullable String error) {
}
