package softwareschreiber.chess.server;

import java.net.InetSocketAddress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.cli.CliGame;
import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.server.packet.GameLoadPacket;

public class ChessServer extends WebSocketServer {
	private ObjectMapper mapper = new ObjectMapper();
	private Game game;

	ChessServer(int port) {
		super(new InetSocketAddress(port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		broadcast("[Server] " + conn + " has left the room!");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		InetSocketAddress remoteAddress = conn.getRemoteSocketAddress();
		broadcast("[Server] Received " + message + " from " + remoteAddress.getHostName() + ":" + remoteAddress.getPort());

		if (message.equalsIgnoreCase(Commands.NEW_GAME.asText())) {
			game = new CliGame(PieceColor.WHITE);
			game.startGame();
			Board board = game.getBoard();

			String[][] pieces = new String[board.getMaxX() + 1][board.getMaxY() + 1];

			for (int x = 0; x <= board.getMaxX(); x++) {
				for (int y = 0; y <= board.getMaxY(); y++) {
					Piece piece = board.getPieceAt(x, y);
					pieces[board.getMaxY() - y][x] = piece == null
							? null
							: Character.toString(piece.getSymbol());
				}
			}

			GameLoadPacket packet = new GameLoadPacket(pieces, game.getActiveColor().name().toLowerCase());

			try {
				conn.send("new-game" + mapper.writeValueAsString(packet));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		Logger.info("Server started on port: " + getPort());
	}
}
