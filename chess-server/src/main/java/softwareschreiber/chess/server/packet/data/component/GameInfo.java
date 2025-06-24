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
public class GameInfo {
	private String id;
	private UserInfo whitePlayer;
	private UserInfo blackPlayer;
	private int maxSecondsPerMove;
}
