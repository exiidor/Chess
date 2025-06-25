package softwareschreiber.chess.server;

import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.server.packet.data.component.GameInfo;

public class ServerGame extends Game {
	private final GameInfo gameInfo;

	public ServerGame(GameInfo gameInfo) {
		super();
		this.gameInfo = gameInfo;
	}

	public GameInfo getInfo() {
		return gameInfo;
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'choosePromotionMove'");
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
}
