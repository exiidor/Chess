package softwareschreiber.chess.engine.history;

import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.player.Player;

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
