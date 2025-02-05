package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Queen extends Piece {

    private String name = "Q";
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
