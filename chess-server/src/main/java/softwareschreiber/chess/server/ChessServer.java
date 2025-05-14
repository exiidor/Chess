package softwareschreiber.chess.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.tinylog.Logger;

import softwareschreiber.chess.cli.CliGame;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.PieceColor;

public class ChessServer extends WebSocketServer {
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

		if (message.equalsIgnoreCase("new-game")) {
			Game game = new CliGame(PieceColor.WHITE);
			game.startGame();
			conn.send("new-game"+game.getBoard());
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
