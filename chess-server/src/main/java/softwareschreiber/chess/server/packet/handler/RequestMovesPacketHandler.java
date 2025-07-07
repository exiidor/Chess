package softwareschreiber.chess.server.packet.handler;

import java.util.Set;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.ConnectionManager;
import softwareschreiber.chess.server.GamesManager;
import softwareschreiber.chess.server.PacketMapper;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.ServerGame;
import softwareschreiber.chess.server.ServerPlayer;
import softwareschreiber.chess.server.packet.c2s.RequestMovesC2S;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.MovesS2C;

public class RequestMovesPacketHandler implements PacketHandler<RequestMovesC2S> {
	private final ChessServer server;

	public RequestMovesPacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.RequestMovesC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		PacketMapper mapper = server.mapper;
		ConnectionManager connections = server.connections;
		GamesManager gameManager = server.gameManager;

		RequestMovesC2S requestMovesPacket = null;

		try {
			requestMovesPacket = mapper.fromString(json, RequestMovesC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

		UserInfo user = connections.getUser(conn.getRemoteSocketAddress());
		ServerGame game = gameManager.getGame(user.gameId());

		if (game == null) {
			Logger.warn("{} tried to request moves, but is not in a game", user.username());
			return;
		}

		int x = requestMovesPacket.data().x();
		int y = requestMovesPacket.data().y();
		Set<? extends Move> moves = game.getBoard().getPieceAt(x, y).getSafeMoves();
		ServerPlayer player = (ServerPlayer) game.getPlayer(user);

		if (player != null) { // null if the player is a spectator
			player.setLastTransmittedMoves(moves);
		}

		MovesS2C responsePacket = new MovesS2C(moves);
		conn.send(mapper.toString(responsePacket));
	}
}
