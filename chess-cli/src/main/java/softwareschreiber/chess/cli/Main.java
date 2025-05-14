package softwareschreiber.chess.cli;

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.PieceColor;

public class Main {
	public static void main(String[] args) {
		Game game = new CliGame(PieceColor.WHITE);
		game.startGame();
		System.out.println(game.getBoard());
	}
}
