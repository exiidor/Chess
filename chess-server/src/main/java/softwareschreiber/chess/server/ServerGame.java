package softwareschreiber.chess.server;

import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.MateKind;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.player.ComputerPlayer;
import softwareschreiber.chess.engine.player.Player;
import softwareschreiber.chess.server.packet.component.GameInfo;
import softwareschreiber.chess.server.packet.component.UserInfo;
import softwareschreiber.chess.server.packet.component.UserInfo.Status;
import softwareschreiber.chess.server.packet.s2c.GameEndedS2C;

public class ServerGame extends Game {
	private final ChessServer server;
	private final GameInfo gameInfo;

	public static ServerGame create(ChessServer server, GameInfo gameInfo) {
		return new ServerGame(
				server,
				gameInfo,
				createPlayerFactory(gameInfo.whitePlayer() != null),
				createPlayerFactory(gameInfo.blackPlayer() != null));
	}

	private static BiFunction<PieceColor, Game, Player> createPlayerFactory(boolean humanPlayer) {
		return (color, game) -> humanPlayer
				? new ServerPlayer(color, (ServerGame) game)
				: new ComputerPlayer(color);
	}

	private ServerGame(
			ChessServer server,
			GameInfo gameInfo,
			BiFunction<PieceColor, Game, Player> whitePlayerFactory,
			BiFunction<PieceColor, Game, Player> blackPlayerFactory) {
		super(whitePlayerFactory, blackPlayerFactory);
		this.server = server;
		this.gameInfo = gameInfo;

		getBoard().addSubmittedMoveDoneListener((piece, move) -> {
			if (getActivePlayer() instanceof ComputerPlayer) {
				server.broadcastBoard(this);
			}
		});
	}

	public GameInfo getInfo() {
		return gameInfo;
	}

	@Override
	public void startGame() {
		super.startGame();
		getBoard().addSubmittedMoveDoneListener(((piece, move) -> {
			if (getActivePlayer() instanceof ComputerPlayer computerPlayer) {
				Move chosenMove = computerPlayer.chooseMove(getBoard());
				Position sourcePos = chosenMove.getSourcePos();
				Piece pieceToMove = getBoard().getPieceAt(sourcePos);

				getBoard().move(pieceToMove, chosenMove, computerPlayer);
			}
		}));
	}

	@Override
	protected void checkMate(PieceColor winningColor) {
		UserInfo winningUser = getUser(winningColor);
		UserInfo losingUser = getUser(winningColor.getOpposite());
		GameEndedS2C packet = new GameEndedS2C(new GameEndedS2C.Data(
				MateKind.CHECKMATE,
				winningUser));
		String json = server.mapper.toString(packet);

		for (UserInfo user : server.getInGameUsers(this)) {
			server.connections.get(user).send(json);
		}

		if (winningUser != null) {
			winningUser.gamesWon(winningUser.gamesWon() + 1);
			winningUser.status(Status.ONLINE);
		}

		if (losingUser != null) {
			losingUser.gamesLost(losingUser.gamesLost() + 1);
			losingUser.status(Status.ONLINE);
		}

		server.gameManager.removeGame(gameInfo);
		server.broadcastUserList();
	}

	@Override
	protected void staleMate() {
		GameEndedS2C packet = new GameEndedS2C(new GameEndedS2C.Data(
				MateKind.STALEMATE,
				null));
		String json = server.mapper.toString(packet);

		for (UserInfo user : server.getInGameUsers(this)) {
			server.connections.get(user).send(json);
		}

		UserInfo whiteUser = gameInfo.whitePlayer();
		UserInfo blackUser = gameInfo.blackPlayer();

		if (whiteUser != null) {
			whiteUser.gamesDrawn(whiteUser.gamesDrawn() + 1);
			whiteUser.status(Status.ONLINE);
		}

		if (blackUser != null) {
			blackUser.gamesDrawn(blackUser.gamesDrawn() + 1);
			blackUser.status(Status.ONLINE);
		}

		server.gameManager.removeGame(gameInfo);
		server.broadcastUserList();
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

	public UserInfo getUser(PieceColor color) {
		return color == PieceColor.WHITE
				? gameInfo.whitePlayer()
				: gameInfo.blackPlayer();
	}
}
