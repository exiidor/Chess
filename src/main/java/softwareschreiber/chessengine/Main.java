package softwareschreiber.chessengine;

import softwareschreiber.chessengine.gamepieces.Queen;

public class Main {
	public static void main(String[] args) {
		Board board = new Board();
		board.initializeStartingPositions();

		Piece piece = board.addPiece(5, 5, new Queen(true, board));

		board.printBoard();
		piece.getValidMoves().forEach(move -> System.out.println(move));
		System.out.println("Es sind " + piece.getValidMoves().size() + " Züge möglich.");
	}
}
