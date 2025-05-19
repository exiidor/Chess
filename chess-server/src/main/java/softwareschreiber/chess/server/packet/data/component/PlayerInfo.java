package softwareschreiber.chess.server.packet.data.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter(onMethod = @__(@JsonProperty))
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PlayerInfo {
	private String username;
	private Status status;
	private boolean playing;
	private int gamesPlayed;
	private int gamesWon;
	private int gamesLost;
	private int gamesDrawn;

	public enum Status {
		OFFLINE,
		ONLINE,
		IN_GAME
	}
}
