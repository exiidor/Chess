package main.java.gamelogic.gamepieces;

import main.java.gamelogic.Piece;

public class Knight extends Piece {

    private String name = "k";
    public Knight(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public String getName(){
        return name;
    }
}
