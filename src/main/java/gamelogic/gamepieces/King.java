package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class King extends Piece {

    private String name = "K";
    private boolean isWhite;

    public King(boolean isWhite) {
        super(isWhite);
        this.isWhite = isWhite;
    }

    @Override
    public String getName() {
        return name;
    }
}
