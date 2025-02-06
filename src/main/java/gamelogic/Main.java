package main.java.gamelogic;

import main.java.gamelogic.gamepieces.Knight;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.initializeStartingPosition();

        Piece piece = board.addPiece(5, 5, new Knight(true, board));

        board.printBoard();
        System.out.println(piece.getValidMoves());
        System.out.println(piece.getValidMoves().size());
    }
}
