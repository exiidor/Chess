package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Rook extends Piece {

    private String name = "R";
    private boolean isWhite;

    public Rook(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
