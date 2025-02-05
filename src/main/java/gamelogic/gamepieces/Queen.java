package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Queen extends Piece {

    private String name = "Q";
    private boolean isWhite;

    public Queen(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
