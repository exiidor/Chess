package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;

public interface PacketHandler<T extends Packet<?>> {
	PacketType getPacketType();
	void handlePacket(String packet, WebSocket conn) throws Exception;
}
