package softwareschreiber.chess.cli;

import softwareschreiber.chess.engine.Game;

public class Main {
	public static void main(String[] args) {
		Game game = new CliGame();
		game.startGame();
		System.out.println(game.getBoard());

		while (!game.isGameOver()) {
			game.getPlayer(game.getActiveColor()).chooseMove(game.getBoard());
		}
	}
}
