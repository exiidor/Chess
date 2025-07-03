package softwareschreiber.chess.server;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.Player;

public class ServerPlayer extends Player {
	private final ServerGame game;

	public ServerPlayer(ServerGame game, PieceColor pieceColor) {
		super(pieceColor);
		this.game = game;
	}

	@Override
	public Move chooseMove(Board board) {
		// TODO: Inform client to choose a move

		throw new UnsupportedOperationException("Unimplemented method 'chooseMove'");
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'choosePromotionMove'");
	}
}
