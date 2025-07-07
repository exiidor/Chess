package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.PacketMapper;
import softwareschreiber.chess.server.UserStore;
import softwareschreiber.chess.server.packet.PacketType;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;

public class LoginPacketHandler implements PacketHandler<LoginC2S> {
	private final ChessServer server;

	public LoginPacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.LoginC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		PacketMapper mapper = server.mapper;
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
			UserStore userStore = server.userStore;
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

				server.connections.add(userInfo, conn);
			}
		}

		LoginResultS2C loginResultPacket = new LoginResultS2C(new LoginResultS2C.Data(errorMessage));
		conn.send(mapper.toString(loginResultPacket));

		if (errorMessage == null) {
			server.broadcastUserList();
			server.sendGames(conn);
			Logger.info("{} has logged in as {}", server.ipPlusPort(conn.getRemoteSocketAddress()), loginPacket.data().username());
		} else {
			Logger.info("{} has failed to log in{}: {}",
					server.ipPlusPort(conn.getRemoteSocketAddress()),
					loginPacket == null ? null : " as " + loginPacket.data().username(),
					errorMessage);
		}
	}
}
