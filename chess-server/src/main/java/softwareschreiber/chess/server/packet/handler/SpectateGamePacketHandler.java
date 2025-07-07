package softwareschreiber.chess.server.packet.handler;

import java.util.List;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.ConnectionManager;
import softwareschreiber.chess.server.PacketMapper;
import softwareschreiber.chess.server.ServerGame;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.SpectateGameC2S;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.component.UserInfo.Status;
import softwareschreiber.chess.server.packet.s2c.GameS2C;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;

public class SpectateGamePacketHandler implements PacketHandler<SpectateGameC2S> {
	private final ChessServer server;

	public SpectateGamePacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.SpectateGameC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		PacketMapper mapper = server.mapper;
		ConnectionManager connections = server.connections;
		SpectateGameC2S spectateGamePacket = null;

		try {
			spectateGamePacket = mapper.fromString(json, SpectateGameC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

		String gameId = spectateGamePacket.data().gameId();
		ServerGame game = server.gameManager.getGame(gameId);

		if (game == null) {
			Logger.warn("{} tried to spectate game {}, but it does not exist", conn.getRemoteSocketAddress(), gameId);
			return;
		}

		if (!game.getInfo().spectatingEnabled()) {
			Logger.warn("{} tried to spectate game {}, but spectating is not enabled", conn.getRemoteSocketAddress(), gameId);
			return;
		}

		UserInfo user = connections.getUser(conn.getRemoteSocketAddress());
		JoinGameS2C joinGamePacket = new JoinGameS2C(new JoinGameS2C.Data(gameId));
		conn.send(mapper.toString(joinGamePacket));

		Logger.info("{} has joined game {} as a spectator", user.username(), gameId);

		UserJoinedS2C userJoinedPacket = new UserJoinedS2C(user);
		List<UserInfo> inGameUsers = server.getInGameUsers(game);

		for (UserInfo inGameUser : inGameUsers) {
			try {
				connections.get(inGameUser).send(mapper.toString(userJoinedPacket));
			} catch (Exception e) {
				server.failedToSerialize(userJoinedPacket, e);
			}
		}

		user.status(Status.SPECTATING);
		user.gameId(gameId);
		GameS2C gamePacket = new GameS2C(game.getInfo());

		conn.send(mapper.toString(gamePacket));
		server.sendBoard(conn, game);
		server.broadcastUserList();
	}
}
