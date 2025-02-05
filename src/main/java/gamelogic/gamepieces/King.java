package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class King extends Piece {

    private String name = "K";
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
