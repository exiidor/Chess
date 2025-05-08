package softwareschreiber.chess.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.PromotionMove;

public class CliGame extends Game {
	public CliGame(PieceColor startingColor) {
		super(startingColor);
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		StringBuilder messageBuilder = new StringBuilder("Choose piece to promote to:");
		Map<Character, PromotionMove> movesBySymbol = new HashMap<>();

		for (PromotionMove move : moves) {
			Piece replacement = move.getReplacement();

			movesBySymbol.put(replacement.getSymbol(), move);
			messageBuilder.append("\n")
					.append(replacement.getName())
					.append(": ")
					.append(replacement.getSymbol());
		}

		System.out.println(messageBuilder.toString());

		while (true) {
			String choice = null;

			try (Scanner scanner = new Scanner(System.in)) {
				choice = scanner.nextLine();
			}

			if (choice.length() == 1) {
				PromotionMove selected = movesBySymbol.get(choice.charAt(0));

				if (selected != null) {
					return selected;
				}
			}

			System.out.println("Invalid choice");
		}
	}

	@Override
	public void checkMate(PieceColor winningColor) {
		System.out.println("Das Spiel ist vorbei " + winningColor + " hat gewonnen");
		endGame();
	}

	@Override
	public void staleMate() {
		System.out.println("Es ist unentschieden");
		endGame();
	}
}
