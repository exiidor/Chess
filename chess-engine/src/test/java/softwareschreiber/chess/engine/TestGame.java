package softwareschreiber.chess.engine;

import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.player.ComputerPlayer;

public class TestGame extends Game {
	public TestGame() {
		super((color, game) -> new TestPlayer(color),
				(color, game) -> new ComputerPlayer(color));
	}

	@Override
	protected void checkMate(PieceColor winningColor) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void staleMate() {
		throw new UnsupportedOperationException();
	}
}
