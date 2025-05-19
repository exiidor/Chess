package softwareschreiber.chess.server.packet;

import softwareschreiber.chess.server.PacketType;

public interface Packet<T> {
	PacketType type();
	T data();
}
