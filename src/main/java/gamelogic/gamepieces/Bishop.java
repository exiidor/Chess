package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Bishop extends Piece {

    private String name = "B";
    private boolean isWhite;

    public Bishop(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
