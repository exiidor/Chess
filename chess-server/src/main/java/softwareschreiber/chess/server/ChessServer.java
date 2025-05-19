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

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.server.packet.c2s.LoginC2S;
import softwareschreiber.chess.server.packet.data.component.PlayerInfo;
import softwareschreiber.chess.server.packet.data.s2c.LoginResultS2CData;
import softwareschreiber.chess.server.packet.s2c.LoginResultS2C;
import softwareschreiber.chess.server.packet.s2c.UserListS2C;

public class ChessServer extends WebSocketServer {
	private static final ObjectMapper mapper = new ObjectMapper();
	private final Map<InetSocketAddress, PlayerInfo> clients = new HashMap<>();
	private Game game;

	ChessServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Logger.info("{} has established a connection", ipPlusPort(conn.getRemoteSocketAddress()));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();

		Logger.info("{} has left the room", ipPlusPort(remoteAddress));
		clients.remove(remoteAddress);
		broadcastClientList();
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

		if (!node.has("type")) {
			Logger.error("Invalid packet: \"{}\"", message);
			return;
		}

		String packetType = node.get("type").asText();

		if (packetType.equalsIgnoreCase(PacketType.LoginC2S.jsonName())) {
			LoginC2S loginPacket = null;
			String errorMessage = null;

			try {
				loginPacket = mapper.treeToValue(node, LoginC2S.class);
			} catch (JsonProcessingException e) {
				Logger.error("Failed to parse login packet \"{}\": {}", message, e);
				errorMessage = e.getMessage();
			}

			if (errorMessage == null) {
				Logger.info("Client logged in: {}", loginPacket.data().username());

				PlayerInfo playerInfo = new PlayerInfo(
						loginPacket.data().username(),
						"online",
						false,
						0,
						0,
						0,
						0);
				clients.put(conn.getRemoteSocketAddress(), playerInfo);
			}

			LoginResultS2C loginResultPacket = new LoginResultS2C(
					PacketType.LoginResultS2C,
					new LoginResultS2CData(errorMessage == null, errorMessage));

			try {
				conn.send(mapper.writeValueAsString(loginResultPacket));
			} catch (JsonProcessingException e) {
				Logger.error("Failed to send login result packet \"{}\": {}", e);
			}

			broadcastClientList();
		} else {
			Logger.warn("Unknown packet type: {}", packetType);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		Logger.info("Server started on port: {}", getPort());
	}

	private String ipPlusPort(InetSocketAddress address) {
		return address.getHostName() + ":" + address.getPort();
	}

	private void broadcastClientList() {
		UserListS2C userListPacket = new UserListS2C(
				PacketType.UserListS2C,
				clients.values());

		try {
			broadcast(mapper.writeValueAsString(userListPacket));
		} catch (JsonProcessingException e) {
			Logger.error("Failed to send user list packet \"{}\": {}", e);
		}
	}
}
