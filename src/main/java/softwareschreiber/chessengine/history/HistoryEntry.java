package softwareschreiber.chessengine.history;

import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.player.Player;

public class HistoryEntry {
	private final Player player;
	private final Piece piece;
	private final Move move;

	public static final HistoryEntry EMPTY = new HistoryEntry(null, null, null);

	public HistoryEntry(Player player, Piece piece, Move move) {
		this.player = player;
		this.piece = piece;
		this.move = move;
	}

	public Player getPlayer() {
		return player;
	}

	public Piece getPiece() {
		return piece;
	}

	public Move getMove() {
		return move;
	}
}
