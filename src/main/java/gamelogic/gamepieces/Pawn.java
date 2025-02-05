package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Pawn extends Piece {

    private String name = "P";
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
