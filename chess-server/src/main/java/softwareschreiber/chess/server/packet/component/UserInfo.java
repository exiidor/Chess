package softwareschreiber.chess.server.packet.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserInfo {
	private String username;
	private Status status;
	private @Nullable String gameId;
	private int gamesWon;
	private int gamesLost;
	private int gamesDrawn;

	public int gamesPlayed() {
		return gamesWon + gamesLost + gamesDrawn;
	}

	public enum Status {
		OFFLINE,
		ONLINE,
		PLAYING,
		SPECTATING
	}
}
