package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Bishop extends Piece {

    private String name = "B";
    public Bishop(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
