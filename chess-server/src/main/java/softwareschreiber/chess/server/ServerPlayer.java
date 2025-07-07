package softwareschreiber.chess.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.HumanPlayer;

public class ServerPlayer extends HumanPlayer {
	private final ServerGame game;
	private List<? extends Move> lastTransmittedMoves = new ArrayList<>();

	public ServerPlayer(PieceColor pieceColor, ServerGame game) {
		super(pieceColor);
		this.game = game;
	}

	@Override
	public Move chooseMove(Board board) {
		throw new IllegalStateException();
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		throw new IllegalStateException();
	}

	public List<? extends Move> getLastTransmittedMoves() {
		return lastTransmittedMoves;
	}

	public void setLastTransmittedMoves(Collection<? extends Move> moves) {
		lastTransmittedMoves = new ArrayList<>(moves);
	}
}
