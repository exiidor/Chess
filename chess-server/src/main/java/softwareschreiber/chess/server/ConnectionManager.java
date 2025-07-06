package softwareschreiber.chess.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import softwareschreiber.chess.server.packet.component.UserInfo;

public class ConnectionManager {
	private final Map<String, WebSocket> connectionsByUsername = new HashMap<>();
	private final Map<InetSocketAddress, UserInfo> usersByAddress = new HashMap<>();

	public void add(UserInfo user, WebSocket connection) {
		connectionsByUsername.put(user.username(), connection);
		usersByAddress.put(connection.getRemoteSocketAddress(), user);
	}

	public WebSocket remove(UserInfo user) {
		return remove(user.username());
	}

	public WebSocket remove(String username) {
		WebSocket conn = connectionsByUsername.remove(username);
		usersByAddress.remove(conn.getRemoteSocketAddress());
		return conn;
	}

	public UserInfo remove(InetSocketAddress address) {
		UserInfo user = usersByAddress.remove(address);
		connectionsByUsername.remove(user.username());
		return user;
	}

	public WebSocket get(UserInfo user) {
		return get(user.username());
	}

	public WebSocket get(String username) {
		return connectionsByUsername.get(username);
	}

	public UserInfo getUser(InetSocketAddress address) {
		return usersByAddress.get(address);
	}

	public boolean isConnected(UserInfo user) {
		return isConnected(user.username());
	}

	public boolean isConnected(String username) {
		return connectionsByUsername.containsKey(username);
	}

	public boolean isConnected(InetSocketAddress address) {
		return usersByAddress.containsKey(address);
	}
}
