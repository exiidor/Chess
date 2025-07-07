package softwareschreiber.chess.server.packet.handler;

import org.java_websocket.WebSocket;
import org.tinylog.Logger;

import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.server.ChessServer;
import softwareschreiber.chess.server.PacketType;
import softwareschreiber.chess.server.ServerGame;
import softwareschreiber.chess.server.ServerPlayer;
import softwareschreiber.chess.server.packet.c2s.MoveC2S;
import softwareschreiber.chess.server.packet.component.UserInfo;

public class MovePacketHandler implements PacketHandler<MoveC2S> {
	private final ChessServer server;

	public MovePacketHandler(ChessServer server) {
		this.server = server;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.MoveC2S;
	}

	@Override
	public void handlePacket(String json, WebSocket conn) {
		MoveC2S movePacket = null;

		try {
			movePacket = server.mapper.fromString(json, MoveC2S.class);
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

		UserInfo user = server.connections.getUser(conn.getRemoteSocketAddress());
		ServerGame game = server.gameManager.getGame(user.gameId());

		if (game == null) {
			Logger.warn("{} tried to make a move, but is not in a game", user.username());
			return;
		}

		ServerPlayer player = (ServerPlayer) game.getPlayer(user);
		Move move = player.getLastTransmittedMoves().get(movePacket.data().committedMoveIndex());
		Piece piece = game.getBoard().getPieceAt(move.getSourcePos());

		if (game.getActivePlayer() != player) {
			Logger.warn("{} tried to make a move, but it is not their turn", user.username());
			return;
		}

		if (!game.isTimeForTurn(piece)) {
			Logger.warn("{} tried to make a move, but the piece does not belong to them", user.username());
			return;
		}

		game.getBoard().move(piece, move, game.getPlayer(game.getActiveColor()));

		// Related S2C packets are sent by ServerGame
	}
}
