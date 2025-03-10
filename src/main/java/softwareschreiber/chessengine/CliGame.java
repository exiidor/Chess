package softwareschreiber.chessengine;

import java.util.Scanner;

import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;

public class CliGame extends Game {
	public CliGame(PieceColor startingColor) {
		super(startingColor);
	}

	@Override
	public Piece getPromotionTarget(Board board, Pawn pawn) {
		String choice = null;

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Choose piece to promote to: (Queen, Rook, Bishop, Knight)");
			choice = scanner.nextLine();
		}

		return switch (choice) {
			case "Queen" -> new Queen(pawn.getColor(), board);
			case "Rook" -> new Rook(pawn.getColor(), board);
			case "Bishop" -> new Bishop(pawn.getColor(), board);
			case "Knight" -> new Knight(pawn.getColor(), board);
			default -> {
				System.out.println("Invalid choice");
				yield getPromotionTarget(board, pawn);
			}
		};
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
