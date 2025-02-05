package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Pawn extends Piece {

    private String name = "P";
    private boolean isWhite;

    public Pawn(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
