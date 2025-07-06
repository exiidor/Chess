package softwareschreiber.chess.cli;

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.player.ComputerPlayer;

public class CliGame extends Game {
	public CliGame() {
		super((color, game) -> new CliPlayer(color),
				(color, game) -> new ComputerPlayer(color));
	}

	@Override
	public void checkMate(PieceColor winningColor) {
		System.out.println(winningColor + " wins by checkmate!");
		endGame();
	}

	@Override
	public void staleMate() {
		System.out.println("Stalemate! The game is a draw.");
		endGame();
	}
}
