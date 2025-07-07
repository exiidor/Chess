package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.packet.c2s.LeaveGameC2S;

public class LeaveGamePacketHandler implements PacketHandler<LeaveGameC2S> {
	private final ChessServer server;

	public LeaveGamePacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.LeaveGameC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		LeaveGameC2S leaveGamePacket = null;

		try {
			leaveGamePacket = server.mapper.fromString(json, LeaveGameC2S.class);
		} catch (Exception e) {
			Logger.error(e);
		}

		server.leaveGame(conn, leaveGamePacket.data().reason());
	}
}
