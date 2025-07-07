package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.ConnectionManager;
import softwareschreiber.chess.server.GamesManager;
import softwareschreiber.chess.server.PacketMapper;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.UserStore;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C;
import softwareschreiber.chess.server.packet.s2c.InviteS2C;

public class CreateGamePacketHandler implements PacketHandler<CreateGameC2S> {
	private final ChessServer server;

	public CreateGamePacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.CreateGameC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		PacketMapper mapper = server.mapper;
		UserStore userStore = server.userStore;
		GamesManager gameManager = server.gameManager;
		ConnectionManager connections = server.connections;

		CreateGameC2S createGamePacket = null;
		String errorMessage = null;

		try {
			createGamePacket = mapper.fromString(json, CreateGameC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			errorMessage = e.getMessage();
		}

		GameInfo gameInfo = null;
		UserInfo initiator = connections.getUser(conn.getRemoteSocketAddress());
		UserInfo requestedOpponent = null;

		if (errorMessage == null && !createGamePacket.data().cpuOpponent()) {
			String requestedOpponentName = createGamePacket.data().requestedOpponent();
			requestedOpponent = userStore.get(requestedOpponentName);

			if (requestedOpponent == null) {
				errorMessage = "Requested opponent " + requestedOpponentName + " does not exist";
			} else if (requestedOpponent.status() == UserInfo.Status.OFFLINE) {
				errorMessage = "Requested opponent " + requestedOpponentName + " is not online";
			} else if (initiator.username().equals(requestedOpponent.username())) {
				errorMessage = "Cannot play against yourself";
			} else if (gameManager.getGame(initiator.username()) != null) {
				errorMessage = "You are already in a game";
			} else if (gameManager.getGame(requestedOpponent.username()) != null) {
				errorMessage = "Requested opponent " + requestedOpponentName + " is already in a game";
			}
		}

		if (errorMessage == null) {
			gameInfo = gameManager.createOrUpdateStub(createGamePacket.data(), initiator);
		}

		CreateGameResultS2C resultPacket = new CreateGameResultS2C(new CreateGameResultS2C.Data(
				gameInfo == null ? null : gameInfo.id(),
				errorMessage));
		conn.send(mapper.toString(resultPacket));

		if (errorMessage != null) {
			Logger.error("Failed to create game for {}: {}", initiator.username(), errorMessage);
		} else {
			Logger.info("{} has created a game with ID {} against {}",
					initiator.username(),
					gameInfo.id(),
					requestedOpponent == null ? "CPU" : requestedOpponent.username());
			if (createGamePacket.data().cpuOpponent()) {
				server.startGame(gameInfo);
			} else {
				InviteS2C invitePacket = new InviteS2C(new InviteS2C.Data(initiator.username(), gameInfo));
				WebSocket opponentConnection = connections.get(requestedOpponent);
				opponentConnection.send(mapper.toString(invitePacket));
			}
		}
	}
}
