package softwareschreiber.chess.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.data.component.BoardPojo;
import softwareschreiber.chess.server.packet.data.component.GameInfo;
import softwareschreiber.chess.server.packet.data.component.UserInfo;
import softwareschreiber.chess.server.packet.data.component.UserInfo.Status;
import softwareschreiber.chess.server.packet.data.s2c.BoardS2CData;
import softwareschreiber.chess.server.packet.data.s2c.CreateGameResultS2CData;
import softwareschreiber.chess.server.packet.data.s2c.InviteS2CData;
import softwareschreiber.chess.server.packet.data.s2c.JoinGameS2CData;
import softwareschreiber.chess.server.packet.data.s2c.KickS2CData;
import softwareschreiber.chess.server.packet.data.s2c.LoginResultS2CData;
import softwareschreiber.chess.server.packet.s2c.BoardS2C;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C;
import softwareschreiber.chess.server.packet.s2c.InviteS2C;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	private final UserStore userStore;
	private final ConnectionManager connections;
	private final PacketMapper mapper;
	private final GamesManager gameManager;

	ChessServer(int port) {
		super(new InetSocketAddress(port));

		userStore = new UserStore();
		connections = new ConnectionManager();
		mapper = new PacketMapper();
		gameManager = new GamesManager(userStore);
	}

	@Override
	public void onStart() {
		Logger.info("Server started on port: {}", getPort());
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Logger.info("{} has established a connection", ipPlusPort(conn.getRemoteSocketAddress()));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
		UserInfo userInfo = connections.remove(remoteAddress);

		Logger.info("{} has disconnected", ipPlusPort(remoteAddress));

		userInfo.status(UserInfo.Status.OFFLINE);
		broadcastUserList();
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		PacketType packetType = mapper.getType(message);

		switch (packetType) {
			case LoginC2S:
				handleLoginPacket(conn, message);
				break;
			case CreateGameC2S:
				handleCreateGamePacket(conn, message);
				break;
			case InviteResponseC2S:
				handleInviteResponsePacket(conn, message);
				break;
			default:
				Logger.warn("Unhandled C2S packet type: {}", packetType);
				break;
		}
	}

	private void handleLoginPacket(WebSocket conn, String json) {
		LoginC2S loginPacket = null;
		String errorMessage = null;

		try {
			loginPacket = mapper.fromString(json, LoginC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			errorMessage = e.getMessage();
		}

		if (errorMessage == null) {
			String username = loginPacket.data().username();
			String password = loginPacket.data().password();
			UserInfo userInfo = userStore.get(username);

			if (userInfo != null && userInfo.status() != UserInfo.Status.OFFLINE) {
				errorMessage = "Already logged in";
			} else if (userInfo != null && !userStore.isPasswordCorrect(username, password)) {
				errorMessage = "Invalid password";
			} else {
				if (userInfo == null) {
					userInfo = new UserInfo(
							loginPacket.data().username(),
							UserInfo.Status.ONLINE,
							null,
							0,
							0,
							0,
							0);
					userStore.put(userInfo, password);
				} else {
					userInfo.status(UserInfo.Status.ONLINE);
				}

				connections.add(userInfo, conn);
			}
		}

		LoginResultS2C loginResultPacket = new LoginResultS2C(new LoginResultS2CData(errorMessage));
		conn.send(mapper.toString(loginResultPacket));

		if (errorMessage == null) {
			broadcastUserList();
			Logger.info("{} has logged in", loginPacket.data().username());
		} else {
			Logger.info("{} has failed to log in: {}",
					loginPacket == null ? "Unknown user" : loginPacket.data().username(),
					errorMessage);
		}
	}

	private void handleCreateGamePacket(WebSocket conn, String json) {
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

		if (errorMessage == null) {
			requestedOpponent = userStore.get(createGamePacket.data().requestedOpponent());

			if (requestedOpponent == null) {
				errorMessage = "Requested opponent does not exist";
			} else if (requestedOpponent.status() == UserInfo.Status.OFFLINE) {
				errorMessage = "Requested opponent is not online";
			} else if (initiator.username().equals(requestedOpponent.username())) {
				errorMessage = "Cannot play against yourself";
			} else if (gameManager.getGame(initiator.username()) != null) {
				errorMessage = "You are already in a game";
			} else if (gameManager.getGame(requestedOpponent.username()) != null) {
				errorMessage = "Requested opponent is already in a game";
			} else {
				gameInfo = gameManager.createStub(createGamePacket.data(), initiator);
			}
		}

		CreateGameResultS2C resultPacket = new CreateGameResultS2C(new CreateGameResultS2CData(
				gameInfo == null ? null : gameInfo.id(), errorMessage));
		conn.send(mapper.toString(resultPacket));

		if (errorMessage != null) {
			Logger.error("Failed to create game for {}: {}", initiator, errorMessage);
		} else {
			Logger.info("{} has created a game with ID {} against {}",
					initiator.username(), gameInfo.id(), requestedOpponent.username());
			InviteS2C invitePacket = new InviteS2C(new InviteS2CData(initiator.username(), gameInfo));
			WebSocket opponentConnection = connections.get(requestedOpponent);
			opponentConnection.send(mapper.toString(invitePacket));
		}
	}

	private void handleInviteResponsePacket(WebSocket conn, String json) {
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
		} else {
			GameInfo gameInfo = gameManager.getStub(gameId);
			UserInfo invitedUser = connections.getUser(conn.getRemoteSocketAddress());

			if (gameInfo == null) {
				Logger.error("Game with ID {} not found for invite response from {}", gameId, invitedUser.username());
				return;
			} else {
				Logger.info("{} has accepted the game invite for game {}", invitedUser.username(), gameId);
				gameInfo.blackPlayer(invitedUser);

				JoinGameS2C joinGamePacket = new JoinGameS2C(new JoinGameS2CData(gameId));
				conn.send(mapper.toString(joinGamePacket));

				UserInfo invitingUser = gameInfo.whitePlayer();
				UserJoinedS2C userJoinedPacket = new UserJoinedS2C(invitedUser);
				connections.get(invitingUser).send(mapper.toString(userJoinedPacket));

				ServerGame game = gameManager.createGame(gameInfo);
				invitingUser.status(Status.PLAYING);
				invitedUser.status(Status.PLAYING);
				invitingUser.gameId(gameId);
				invitedUser.gameId(gameId);
				broadcastUserList();
				game.startGame();
				broadcastBoard(game);
			}
		}
	}

	private String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	private void broadcastUserList() {
		UserListS2C packet = new UserListS2C(userStore.values());
		String json = mapper.toString(packet);

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				client.send(json);
			}
		}
	}

	private void broadcastBoard(ServerGame game) {
		BoardS2C packet = new BoardS2C(new BoardS2CData(game.getInfo().id(), new BoardPojo(game.getBoard())));
		String json = mapper.toString(packet);

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				UserInfo user = connections.getUser(client.getRemoteSocketAddress());

				if (!game.getInfo().id().equals(user.gameId())) {
					assert user.status() == Status.PLAYING || user.status() == Status.SPECTATING;
					continue;
				}

				try {
					client.send(json);
				} catch (Exception e) {
					failedToSerialize(packet, e);
				}
			}
		}
	}

	void kick(String username) {
		KickS2C packet = new KickS2C(new KickS2CData("Admin", null));
		WebSocket connection = connections.get(username);

		try {
			connection.send(mapper.toString(packet));
		} catch (Exception e) {
			Logger.error(e);
		}

		connection.close();
	}

	private void failedToSerialize(Packet<?> packet, Exception exception) {
		Logger.error("Failed to serialize {} packet \"{}\": {}", packet.getClass().getSimpleName(), packet, exception);
	}
}
