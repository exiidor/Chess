package softwareschreiber.chess.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.server.packet.Packet;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.data.component.UserInfo;
import softwareschreiber.chess.server.packet.data.s2c.KickS2CData;
import softwareschreiber.chess.server.packet.data.s2c.LoginResultS2CData;
import softwareschreiber.chess.server.packet.s2c.KickS2C;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	private final UserStore userStore = new UserStore();
	private final ConnectionManager connections = new ConnectionManager();
	private final PacketMapper mapper = new PacketMapper();

	ChessServer(int port) {
		super(new InetSocketAddress(port));
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
							false,
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

		LoginResultS2C loginResultPacket = new LoginResultS2C(
				PacketType.LoginResultS2C,
				new LoginResultS2CData(errorMessage));

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

	private String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	private void broadcastUserList() {
		UserListS2C packet = new UserListS2C(
				PacketType.UserListS2C,
				userStore.values());
		String json = mapper.toString(packet);

		for (WebSocket client : getConnections()) {
			if (connections.isConnected(client.getRemoteSocketAddress())) {
				client.send(json);
			}
		}
	}

	void kick(String username) {
		KickS2C packet = new KickS2C(PacketType.KickS2C, new KickS2CData("Admin", null));
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
