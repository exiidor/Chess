package softwareschreiber.chess.server.packet.data.s2c;

import org.jetbrains.annotations.Nullable;

public record KickS2CData(String initiator, @Nullable String reason) {
}
