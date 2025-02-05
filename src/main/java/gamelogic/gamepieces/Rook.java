package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Rook extends Piece {

    private String name = "R";
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
