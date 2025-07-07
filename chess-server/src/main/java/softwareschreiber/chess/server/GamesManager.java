package softwareschreiber.chess.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.server.packet.c2s.CreateGameC2S;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;

public class GamesManager {
	private final AtomicInteger idGenerator = new AtomicInteger(0);
	private final Map<String, GameInfo> gameStubsById = new HashMap<>();
	private final Map<String, GameInfo> gameStubsByCreator = new HashMap<>();
	private final Map<String, ServerGame> gamesById = new HashMap<>();
	private final ChessServer server;
	private final UserStore userStore;

	public GamesManager(ChessServer server, UserStore userStore) {
		this.server = server;
		this.userStore = userStore;
	}

	public GameInfo getStub(String gameId) {
		GameInfo gameInfo = gameStubsById.get(gameId);
		return gameInfo;
	}

	public GameInfo getStubByCreator(String creatorUsername) {
		return gameStubsByCreator.get(creatorUsername);
	}

	public ServerGame getGame(String gameId) {
		return gamesById.get(gameId);
	}

	public GameInfo createOrUpdateStub(CreateGameC2S.Data data, UserInfo initiator) {
		assert data.cpuOpponent() || data.requestedOpponent() != null : "Either cpuOpponent or requestedOpponent must be set";

		GameInfo gameInfo = gameStubsById.get(initiator.username());
		UserInfo whitePlayer = data.ownColor() == PieceColor.WHITE
				? initiator
				: userStore.get(data.requestedOpponent());
		UserInfo blackPlayer = data.ownColor() == PieceColor.BLACK
				? initiator
				: userStore.get(data.requestedOpponent());

		if (gameInfo != null) {
			gameInfo.whitePlayer(whitePlayer);
			gameInfo.blackPlayer(blackPlayer);
			gameInfo.maxSecondsPerMove(data.maxSecondsPerMove());
		} else {
			gameInfo = new GameInfo(
					String.valueOf(idGenerator.incrementAndGet()),
					whitePlayer,
					blackPlayer,
					data.maxSecondsPerMove());
			gameStubsById.put(gameInfo.id(), gameInfo);
			gameStubsByCreator.put(initiator.username(), gameInfo);
		}

		return gameInfo;
	}

	public ServerGame createGame(GameInfo info) {
		ServerGame game = ServerGame.create(server, info);
		gameStubsById.remove(info.id());
		gamesById.put(info.id(), game);
		return game;
	}

	public void removeGame(GameInfo info) {
		gameStubsById.remove(info.id());
		gamesById.remove(info.id());

		for (Map.Entry<String, GameInfo> entry : gameStubsByCreator.entrySet()) {
			if (entry.getValue().id().equals(info.id())) {
				gameStubsByCreator.remove(entry.getKey());
				break;
			}
		}
	}
}
