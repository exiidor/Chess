package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Knight extends Piece {

    private String name = "k";
    private boolean isWhite;

    public Knight(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
