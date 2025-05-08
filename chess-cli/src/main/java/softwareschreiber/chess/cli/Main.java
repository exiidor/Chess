package softwareschreiber.chess.cli;

import softwareschreiber.chess.engine.gamepieces.PieceColor;

public class Main {
	public static void main(String[] args) {
		new CliGame(PieceColor.WHITE).startGame();
	}
}
