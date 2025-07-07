package softwareschreiber.chess.server;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.component.BoardPojo;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.component.UserInfo.Status;
import softwareschreiber.chess.server.packet.handler.CreateGamePacketHandler;
import softwareschreiber.chess.server.packet.handler.InviteResponsePacketHandler;
import softwareschreiber.chess.server.packet.handler.LeaveGamePacketHandler;
import softwareschreiber.chess.server.packet.handler.LoginPacketHandler;
import softwareschreiber.chess.server.packet.handler.MovePacketHandler;
import softwareschreiber.chess.server.packet.handler.RequestMovesPacketHandler;
import softwareschreiber.chess.server.packet.handler.SpectateGamePacketHandler;
import softwareschreiber.chess.server.packet.s2c.BoardS2C;
import softwareschreiber.chess.server.packet.s2c.GameS2C;
import softwareschreiber.chess.server.packet.s2c.GamesS2C;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.UserLeftS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	public final UserStore userStore;
	public final ConnectionManager connections;
	public final PacketMapper mapper;
	public final GamesManager gameManager;
	private final LoginPacketHandler loginPacketHandler;
	private final CreateGamePacketHandler createGamePacketHandler;
	private final InviteResponsePacketHandler inviteResponsePacketHandler;
	private final LeaveGamePacketHandler leaveGamePacketHandler;
	private final RequestMovesPacketHandler requestMovesPacketHandler;
	private final MovePacketHandler movePacketHandler;
	private final SpectateGamePacketHandler spectateGamePacketHandler;

	ChessServer(int port) {
		super(new InetSocketAddress(port));

		userStore = new UserStore();
		connections = new ConnectionManager();
		mapper = new PacketMapper();
		gameManager = new GamesManager(this, userStore);

		loginPacketHandler = new LoginPacketHandler(this);
		createGamePacketHandler = new CreateGamePacketHandler(this);
		inviteResponsePacketHandler = new InviteResponsePacketHandler(this);
		leaveGamePacketHandler = new LeaveGamePacketHandler(this);
		requestMovesPacketHandler = new RequestMovesPacketHandler(this);
		movePacketHandler = new MovePacketHandler(this);
		spectateGamePacketHandler = new SpectateGamePacketHandler(this);
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
			Logger.info("{} has disconnected", ipPlusPort(remoteAddress));

			leaveGame(conn, "Client disconnected");
			UserInfo userInfo = connections.remove(remoteAddress);
			userInfo.status(UserInfo.Status.OFFLINE);
			broadcastUserList();
		} else {
			// client wasn't available during server stop and closed the connection at a later time
			Logger.info("{} has disconnected, but was not connected", ipPlusPort(remoteAddress));
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Logger.error("Error on connection {}: {}", ipPlusPort(conn.getRemoteSocketAddress()), ex);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		PacketType packetType = mapper.getType(message);

		switch (packetType) {
			case LoginC2S:
				loginPacketHandler.handlePacket(message, conn);
				break;
			case CreateGameC2S:
				createGamePacketHandler.handlePacket(message, conn);
				break;
			case InviteResponseC2S:
				inviteResponsePacketHandler.handlePacket(message, conn);
				break;
			case LeaveGameC2S:
				leaveGamePacketHandler.handlePacket(message, conn);
				break;
			case RequestMovesC2S:
				requestMovesPacketHandler.handlePacket(message, conn);
				break;
			case MoveC2S:
				movePacketHandler.handlePacket(message, conn);
				break;
			case SpectateGameC2S:
				spectateGamePacketHandler.handlePacket(message, conn);
				break;
			default:
				Logger.warn("Unhandled C2S packet type: {}", packetType);
				break;
		}
	}

	public void startGame(GameInfo gameInfo) {
		ServerGame game = gameManager.createGame(gameInfo);
		UserInfo whiteUser = gameInfo.whitePlayer();
		UserInfo blackUser = gameInfo.blackPlayer();
		String gamePacketJson = mapper.toString(new GameS2C(gameInfo));

		if (whiteUser != null) {
			whiteUser.status(Status.PLAYING);
			whiteUser.gameId(gameInfo.id());
			connections.get(whiteUser).send(gamePacketJson);
		}

		if (blackUser != null) {
			blackUser.status(Status.PLAYING);
			blackUser.gameId(gameInfo.id());
			connections.get(blackUser).send(gamePacketJson);
		}

		broadcastUserList();
		game.startGame();
		broadcastGames();
		broadcastBoard(game);
	}

	public void leaveGame(WebSocket conn, @Nullable String reason) {
		UserInfo user = connections.getUser(conn.getRemoteSocketAddress());
		gameManager.removeGame(gameManager.getStub(user.gameId()));
		ServerGame game = gameManager.getGame(user.gameId());

		if (game == null) {
			return;
		}

		GameInfo gameInfo = game.getInfo();
		Logger.info("{} has left the game {} for reason: {}", user.username(), gameInfo.id(), reason);

		user.status(Status.ONLINE);
		user.gameId(null);

		UserInfo blackUser = gameInfo.blackPlayer();
		UserInfo whiteUser = gameInfo.whitePlayer();

		if ((whiteUser == null || whiteUser.status() != Status.PLAYING)
				&& (blackUser == null || blackUser.status() != Status.PLAYING)) {
			gameManager.removeGame(gameInfo);
			broadcastGames();
		}

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

	public String ipPlusPort(InetSocketAddress address) {
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

	public void broadcastGames() {
		GamesS2C packet = new GamesS2C(gameManager.getGames().stream().map(ServerGame::getInfo).toList());
		String json = mapper.toString(packet);

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				client.send(json);
			}
		}
	}

	public void sendGames(WebSocket client) {
		GamesS2C packet = new GamesS2C(gameManager.getGames().stream().map(ServerGame::getInfo).toList());
		String json = mapper.toString(packet);

		if (connections.isConnected(client.getRemoteSocketAddress())) {
			client.send(json);
		}
	}

	public void broadcastBoard(ServerGame game) {
		BoardS2C packet = new BoardS2C(new BoardS2C.Data(
				game.getInfo().id(),
				new BoardPojo(game.getBoard())));
		String json = mapper.toString(packet);
		List<UserInfo> inGameUsers = getInGameUsers(game);

		for (UserInfo user : inGameUsers) {
			try {
				connections.get(user).send(json);
			} catch (Exception e) {
				failedToSerialize(packet, e);
			}
		}
	}

	public void sendBoard(WebSocket clien, ServerGame game) {
		BoardS2C packet = new BoardS2C(new BoardS2C.Data(
				game.getInfo().id(),
				new BoardPojo(game.getBoard())));
		String json = mapper.toString(packet);

		if (connections.isConnected(clien.getRemoteSocketAddress())) {
			clien.send(json);
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

	public void failedToSerialize(Packet<?> packet, Exception exception) {
		Logger.error("Failed to serialize {} packet \"{}\": {}", packet.getClass().getSimpleName(), packet, exception);
	}
}
