package softwareschreiber.chess.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import softwareschreiber.chess.server.packet.data.c2s.CreateGameC2SData;
import softwareschreiber.chess.server.packet.data.component.GameInfo;
import softwareschreiber.chess.server.packet.data.component.UserInfo;

public class GamesManager {
	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final Map<String, GameInfo> gameStubsById = new HashMap<>();
	private final Map<String, ServerGame> gamesById = new HashMap<>();
	private final UserStore userStore;

	public GamesManager(UserStore userStore) {
		this.userStore = userStore;
	}

	public GameInfo getStub(String gameId) {
		GameInfo gameInfo = gameStubsById.get(gameId);
		return gameInfo;
	}

	public ServerGame getGame(String gameId) {
		return gamesById.get(gameId);
	}

	public GameInfo createStub(CreateGameC2SData data, UserInfo initiator) {
		GameInfo gameInfo = new GameInfo(
				String.valueOf(idGenerator.incrementAndGet()),
				initiator,
				userStore.get(data.requestedOpponent()),
				data.maxSecondsPerMove());
		gameStubsById.put(gameInfo.id(), gameInfo);
		return gameInfo;
	}

	public ServerGame createGame(GameInfo info) {
		ServerGame game = new ServerGame(info);
		gameStubsById.remove(info.id());
		gamesById.put(info.id(), game);
		return game;
	}
}
