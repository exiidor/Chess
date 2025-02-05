package main.java.gamelogic;

public class Piece {
    private String name;
    private boolean isWhite;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public String getName() {
        return name;
    }
}
