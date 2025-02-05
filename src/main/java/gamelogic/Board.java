package main.java.gamelogic;

import main.java.gamelogic.gamepieces.Bishop;
import main.java.gamelogic.gamepieces.King;
import main.java.gamelogic.gamepieces.Knight;
import main.java.gamelogic.gamepieces.Pawn;
import main.java.gamelogic.gamepieces.Queen;
import main.java.gamelogic.gamepieces.Rook;

public class Board {

    private Piece[][] position;

    Board() {
        position = new Piece[8][8];

    }

    void initializeStartingPosition() {

        for (int i = 0; i < 8; i += 7) {
            boolean isWhite = i == 0 ? true : false;
            position[i][0] = new Rook(isWhite);
            position[i][1] = new Knight(isWhite);
            position[i][2] = new Bishop(isWhite);
            position[i][3] = new Queen(isWhite);
            position[i][4] = new King(isWhite);
            position[i][5] = new Bishop(isWhite);
            position[i][6] = new Knight(isWhite);
            position[i][7] = new Rook(isWhite);
            for (int ii = 0; ii < 8; ii++) {
                int offset = isWhite == true ? 1 : -1;
                position[i + offset][ii] = new Pawn(isWhite);
            }
        }
    }

    void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int ii = 0; ii < 8; ii++) {
                if (position[i][ii] != null) {
                    System.out.print(position[i][ii].getName() + " ");
                } else {
                    System.out.print("x ");
                }
            }
            System.out.println();
        }
    }

}
