package softwareschreiber.chess.server;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.player.Player;
import softwareschreiber.chess.server.packet.data.component.GameInfo;
import softwareschreiber.chess.server.packet.data.component.UserInfo;

public class ServerGame extends Game {
	private final GameInfo gameInfo;

	public ServerGame(GameInfo gameInfo) {
		super((color, game) -> new ServerPlayer(color, (ServerGame) game),
				(color, game) -> new ServerPlayer(color, (ServerGame) game));
		this.gameInfo = gameInfo;
	}

	public GameInfo getInfo() {
		return gameInfo;
	}

	@Override
	protected void checkMate(PieceColor winningColor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'checkMate'");
	}

	@Override
	protected void staleMate() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'staleMate'");
	}

	@Nullable
	public Player getPlayer(UserInfo user) {
		if (user.equals(gameInfo.whitePlayer())) {
			return getWhitePlayer();
		} else if (user.equals(gameInfo.blackPlayer())) {
			return getBlackPlayer();
		}

		return null;
	}
}
