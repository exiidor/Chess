package softwareschreiber.chessengine;

import java.util.Scanner;

import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;

public class CliGame extends Game {
	@Override
	protected Piece getPromotionTarget(Board board, Pawn pawn) {
		String choice = null;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Choose piece to promote to: (Queen, Rook, Bishop, Knight)");
			choice = scanner.nextLine();
		}

		switch (choice) {
			case "Queen":
				return new Queen(pawn.isWhite(), board);
			case "Rook":
				return new Rook(pawn.isWhite(), board);
			case "Bishop":
				return new Bishop(pawn.isWhite(), board);
			case "Knight":
				return new Knight(pawn.isWhite(), board);
			default:
				System.out.println("Invalid choice");
				return getPromotionTarget(board, pawn);
		}
	}

	@Override
	public void checkMate(String color) {
		System.out.println("Das Spiel ist vorbei " + color + " hat gewonnen");
		endGame();
	}

	@Override
	public void staleMate() {
		System.out.println("Es ist unentschieden");
		endGame();
	}
}
