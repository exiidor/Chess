package softwareschreiber.chess.server.packet;

import com.fasterxml.jackson.annotation.JsonProperty;

import softwareschreiber.chess.server.PacketType;

public interface Packet<T> {
	@JsonProperty(required = true, index = 0)
	PacketType type();

	@JsonProperty(required = true, index = 1)
	T data();
}
