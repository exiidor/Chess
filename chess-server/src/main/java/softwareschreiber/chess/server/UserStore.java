package softwareschreiber.chess.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import softwareschreiber.chess.server.packet.component.UserInfo;

public class UserStore {
	private final Map<String, UserInfo> usersByName = new HashMap<>();
	private final Map<String, String> passwordsByUsername = new HashMap<>();

	public void put(UserInfo user, String password) {
		usersByName.put(user.username(), user);
		passwordsByUsername.put(user.username(), password);
	}

	public UserInfo get(String username) {
		return usersByName.get(username);
	}

	public boolean isRegistered(String username) {
		return usersByName.containsKey(username);
	}

	public boolean isPasswordCorrect(String username, String password) {
		return passwordsByUsername.get(username).equals(password);
	}

	public Collection<UserInfo> values() {
		return usersByName.values();
	}
}
