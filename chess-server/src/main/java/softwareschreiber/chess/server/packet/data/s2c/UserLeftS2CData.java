package softwareschreiber.chess.server.packet.data.s2c;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.server.packet.data.component.UserInfo;

public record UserLeftS2CData(UserInfo user, @Nullable String reason) {
}
