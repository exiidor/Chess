package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.ConnectionManager;
import softwareschreiber.chess.server.GamesManager;
import softwareschreiber.chess.server.PacketMapper;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;

public class InviteResponsePacketHandler implements PacketHandler<InviteResponseC2S> {
	private final ChessServer server;

	public InviteResponsePacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.InviteResponseC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		PacketMapper mapper = server.mapper;
		ConnectionManager connections = server.connections;
		GamesManager gameManager = server.gameManager;

		InviteResponseC2S inviteResponsePacket = null;

		try {
			inviteResponsePacket = mapper.fromString(json, InviteResponseC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

		boolean accepted = inviteResponsePacket.data().accept();
		String gameId = inviteResponsePacket.data().gameId();

		if (!accepted) {
			Logger.info("{} has declined the game invite for game {}", conn.getRemoteSocketAddress(), gameId);
			return;
		}

		GameInfo gameInfo = gameManager.getStub(gameId);
		UserInfo invitedUser = connections.getUser(conn.getRemoteSocketAddress());

		if (gameInfo == null) {
			Logger.error("Game with ID {} not found for invite response from {}", gameId, invitedUser.username());
			return;
		}

		server.leaveGame(conn, json);
		Logger.info("{} has accepted the game invite for game {}", invitedUser.username(), gameId);
		UserInfo invitingUser;

		if (invitedUser == gameInfo.whitePlayer()) {
			invitingUser = gameInfo.blackPlayer();
			gameInfo.whitePlayer(invitedUser);
		} else if (invitedUser == gameInfo.blackPlayer()) {
			invitingUser = gameInfo.whitePlayer();
			gameInfo.blackPlayer(invitedUser);
		} else {
			Logger.error("User {} is not part of the game {}", invitedUser.username(), gameId);
			return;
		}

		JoinGameS2C joinGamePacket = new JoinGameS2C(new JoinGameS2C.Data(gameId));
		conn.send(mapper.toString(joinGamePacket));

		UserJoinedS2C userJoinedPacket = new UserJoinedS2C(invitedUser);
		connections.get(invitingUser).send(mapper.toString(userJoinedPacket));

		server.startGame(gameInfo);
	}
}
