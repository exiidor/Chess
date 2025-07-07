package softwareschreiber.chess.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.c2s.InviteResponseC2S;
import softwareschreiber.chess.server.packet.c2s.LeaveGameC2S;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.c2s.MoveC2S;
import softwareschreiber.chess.server.packet.c2s.RequestMovesC2S;
import softwareschreiber.chess.server.packet.component.BoardPojo;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.component.UserInfo.Status;
import softwareschreiber.chess.server.packet.s2c.BoardS2C;
import softwareschreiber.chess.server.packet.s2c.CreateGameResultS2C;
import softwareschreiber.chess.server.packet.s2c.InviteS2C;
import softwareschreiber.chess.server.packet.s2c.JoinGameS2C;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.MovesS2C;
import softwareschreiber.chess.server.packet.s2c.UserJoinedS2C;
import softwareschreiber.chess.server.packet.s2c.UserLeftS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	public final UserStore userStore;
	public final ConnectionManager connections;
	public final PacketMapper mapper;
	public final GamesManager gameManager;

	ChessServer(int port) {
		super(new InetSocketAddress(port));

		userStore = new UserStore();
		connections = new ConnectionManager();
		mapper = new PacketMapper();
		gameManager = new GamesManager(this, userStore);
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

		if (connections.isConnected(remoteAddress)) {
			UserInfo userInfo = connections.remove(remoteAddress);

			Logger.info("{} has disconnected", ipPlusPort(remoteAddress));

			userInfo.status(UserInfo.Status.OFFLINE);
			broadcastUserList();
		} else {
			// client wasn't available during server stop and closed the connection at a later time
			Logger.info("{} has disconnected, but was not connected", ipPlusPort(remoteAddress));
		}
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
			case LeaveGameC2S:
				handleLeaveGamePacket(conn, message);
				break;
			case RequestMovesC2S:
				handleRequestMovesPacket(conn, message);
				break;
			case MoveC2S:
				handleMovePacket(conn, message);
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
							0);
					userStore.put(userInfo, password);
				} else {
					userInfo.status(UserInfo.Status.ONLINE);
				}

				connections.add(userInfo, conn);
			}
		}

		LoginResultS2C loginResultPacket = new LoginResultS2C(new LoginResultS2C.Data(errorMessage));
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
				startGame(gameInfo);
			} else {
				InviteS2C invitePacket = new InviteS2C(new InviteS2C.Data(initiator.username(), gameInfo));
				WebSocket opponentConnection = connections.get(requestedOpponent);
				opponentConnection.send(mapper.toString(invitePacket));
			}
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

				JoinGameS2C joinGamePacket = new JoinGameS2C(new JoinGameS2C.Data(gameId));
				conn.send(mapper.toString(joinGamePacket));

				UserInfo invitingUser = gameInfo.whitePlayer();
				UserJoinedS2C userJoinedPacket = new UserJoinedS2C(invitedUser);
				connections.get(invitingUser).send(mapper.toString(userJoinedPacket));

				startGame(gameInfo);
			}
		}
	}

	private void startGame(GameInfo gameInfo) {
		ServerGame game = gameManager.createGame(gameInfo);
		UserInfo whiteUser = gameInfo.whitePlayer();
		UserInfo blackUser = gameInfo.blackPlayer();

		if (whiteUser != null) {
			whiteUser.status(Status.PLAYING);
			whiteUser.gameId(gameInfo.id());
		}

		if (blackUser != null) {
			blackUser.status(Status.PLAYING);
			blackUser.gameId(gameInfo.id());
		}

		broadcastUserList();
		game.startGame();
		broadcastBoard(game);
	}

	private void handleLeaveGamePacket(WebSocket conn, String json) {
		LeaveGameC2S leaveGamePacket = null;

		try {
			leaveGamePacket = mapper.fromString(json, LeaveGameC2S.class);
		} catch (Exception e) {
			Logger.error(e);
		}

		UserInfo user = connections.getUser(conn.getRemoteSocketAddress());
		ServerGame game = gameManager.getGame(user.gameId());

		if (game == null) {
			Logger.warn("{} tried to leave a game, but is not in one", user.username());
			return;
		}

		GameInfo gameInfo = game.getInfo();
		String reason = leaveGamePacket.data().reason();
		Logger.info("{} has left the game {} for reason: {}", user.username(), gameInfo.id(), reason);
		UserInfo blackUser = gameInfo.blackPlayer();
		UserInfo whiteUser = gameInfo.whitePlayer();

		if ((whiteUser == null || whiteUser.status() != Status.PLAYING)
				&& (blackUser == null || blackUser.status() != Status.PLAYING)) {
			gameManager.removeGame(gameInfo);
		}

		user.status(Status.ONLINE);
		user.gameId(null);

		UserLeftS2C userLeftPacket = new UserLeftS2C(new UserLeftS2C.Data(user, reason));
		List<UserInfo> players = getInGameUsers(game);

		for (UserInfo player : players) {
			if (player == user) {
				continue;
			}

			try {
				connections.get(player).send(mapper.toString(userLeftPacket));
			} catch (Exception e) {
				failedToSerialize(userLeftPacket, e);
			}
		}

		broadcastUserList();
	}

	private void handleRequestMovesPacket(WebSocket conn, String json) {
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
		player.setLastTransmittedMoves(moves);

		MovesS2C responsePacket = new MovesS2C(moves);
		conn.send(mapper.toString(responsePacket));
	}

	private void handleMovePacket(WebSocket conn, String json) {
		MoveC2S movePacket = null;

		try {
			movePacket = mapper.fromString(json, MoveC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

		UserInfo user = connections.getUser(conn.getRemoteSocketAddress());
		ServerGame game = gameManager.getGame(user.gameId());

		if (game == null) {
			Logger.warn("{} tried to make a move, but is not in a game", user.username());
			return;
		}

		ServerPlayer player = (ServerPlayer) game.getPlayer(user);
		Move move = player.getLastTransmittedMoves().get(movePacket.data().committedMoveIndex());
		Piece piece = game.getBoard().getPieceAt(move.getSourcePos());

		if (game.getActivePlayer() != player) {
			Logger.warn("{} tried to make a move, but it is not their turn", user.username());
			return;
		}

		if (!game.isTimeForTurn(piece)) {
			Logger.warn("{} tried to make a move, but the piece does not belong to them", user.username());
			return;
		}

		game.getBoard().move(piece, move, game.getPlayer(game.getActiveColor()));
	}

	private String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	public void broadcastUserList() {
		UserListS2C packet = new UserListS2C(userStore.values());
		String json = mapper.toString(packet);

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				client.send(json);
			}
		}
	}

	public void broadcastBoard(ServerGame game) {
		BoardS2C packet = new BoardS2C(new BoardS2C.Data(
				game.getInfo().id(),
				new BoardPojo(game.getBoard())));
		String json = mapper.toString(packet);
		List<UserInfo> inGameUsers = getInGameUsers(game);

		for (UserInfo user : inGameUsers) {
			if (!game.getInfo().id().equals(user.gameId())) {
				assert user.status() == Status.PLAYING || user.status() == Status.SPECTATING;
				continue;
			}

			try {
				connections.get(user).send(json);
			} catch (Exception e) {
				failedToSerialize(packet, e);
			}
		}
	}

	public List<UserInfo> getInGameUsers(ServerGame game) {
		List<UserInfo> users = new ArrayList<>();

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				UserInfo user = connections.getUser(client.getRemoteSocketAddress());

				if (game.getInfo().id().equals(user.gameId())) {
					assert user.status() == Status.PLAYING || user.status() == Status.SPECTATING;
					users.add(user);
				}
			}
		}

		return users;
	}

	void kick(String username) {
		KickS2C packet = new KickS2C(new KickS2C.Data("Admin", null));
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
