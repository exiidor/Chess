package softwareschreiber.chess.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.data.component.PlayerInfo;
import softwareschreiber.chess.server.packet.data.s2c.LoginResultS2CData;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	private static final ObjectMapper mapper = new ObjectMapper();
	private final Map<InetSocketAddress, PlayerInfo> clientsByAddress = new HashMap<>();
	private final Map<String, PlayerInfo> clientsByUsername = new HashMap<>();
	private final Map<String, String> passwordByUsername = new HashMap<>();

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

		Logger.info("{} has disconnected", ipPlusPort(remoteAddress));
		clientsByAddress.remove(remoteAddress).status(PlayerInfo.Status.OFFLINE);
		broadcastClientList();
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		Logger.debug("Received \"{}\" from {}", message, ipPlusPort(conn.getRemoteSocketAddress()));
		JsonNode node;

		try {
			node = mapper.readTree(message);
		} catch (JsonProcessingException e) {
			Logger.error("Failed to parse \"{}\": {}", message, e);
			return;
		}

		JsonNode packetTypeJsonNode = node.get("key");

		if (packetTypeJsonNode == null) {
			Logger.error("Invalid packet: \"{}\"", message);
			return;
		}

		PacketType packetType = PacketType.fromJsonName(packetTypeJsonNode.asText());

		switch (packetType) {
			case LoginC2S:
				handleLoginPacket(conn, node);
				break;
			default:
				Logger.warn("Unknown packet type: {}", packetType);
				break;
		}
	}

	private void handleLoginPacket(WebSocket conn, JsonNode node) {
		LoginC2S loginPacket = null;
		String errorMessage = null;

		try {
			loginPacket = mapper.treeToValue(node, LoginC2S.class);
		} catch (JsonProcessingException e) {
			Logger.error("Failed to parse login packet \"{}\": {}", node.toPrettyString(), e);
			errorMessage = e.getMessage();
		}

		if (errorMessage == null) {
			String username = loginPacket.data().username();
			String password = loginPacket.data().password();

			Logger.info("Client logged in: {}", username);
			PlayerInfo playerInfo = clientsByUsername.get(username);

			if (playerInfo != null && playerInfo.status() != PlayerInfo.Status.OFFLINE) {
				errorMessage = "Already logged in";
			} else if (playerInfo != null && !password.equals(passwordByUsername.get(username))) {
				errorMessage = "Invalid password";
			} else {
				if (playerInfo == null) {
					playerInfo = new PlayerInfo(
							loginPacket.data().username(),
							PlayerInfo.Status.ONLINE,
							false,
							0,
							0,
							0,
							0);
					clientsByUsername.put(username, playerInfo);
				} else {
					playerInfo.status(PlayerInfo.Status.ONLINE);
				}

				clientsByAddress.put(conn.getRemoteSocketAddress(), playerInfo);
				passwordByUsername.put(playerInfo.username(), password);
			}
		}

		LoginResultS2C loginResultPacket = new LoginResultS2C(
				PacketType.LoginResultS2C,
				new LoginResultS2CData(errorMessage == null, errorMessage));

		try {
			conn.send(mapper.writeValueAsString(loginResultPacket));
		} catch (JsonProcessingException e) {
			Logger.error("Failed to serialize login result packet \"{}\": {}", loginResultPacket, e);
		}

		if (errorMessage == null) {
			broadcastClientList();
		}
	}

	private String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	private void broadcastClientList() {
		UserListS2C userListPacket = new UserListS2C(
				PacketType.UserListS2C,
				clientsByUsername.values());

		try {
			for (WebSocket client : getConnections()) {
				if (clientsByAddress.containsKey(client.getRemoteSocketAddress())) {
					client.send(mapper.writeValueAsString(userListPacket));
				}
			}
		} catch (JsonProcessingException e) {
			Logger.error("Failed to serialize user list packet \"{}\": {}", userListPacket, e);
		}
	}
}
