package main.java.gamelogic.gamepieces;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import main.java.gamelogic.Board;
import main.java.gamelogic.Piece;
import main.java.gamelogic.Position;

public class King extends Piece {
    public King(boolean isWhite, Board board) {
        super(isWhite, board);
    }

    @Override
    public char getSymbol() {
        return 'K';
    }

    @Override
    public Set<Position> getValidMoves() {
        Set<Position> validMoves = new LinkedHashSet<>();

        Position forward = new Position(getX(), getY() + 1);
        Position forwardRight = new Position(getX() + 1, getY() + 1);
        Position right = new Position(getX() + 1, getY());
        Position backRight = new Position(getX() + 1, getY() - 1);
        Position back = new Position(getX(), getY() - 1);
        Position backLeft = new Position(getX() - 1, getY() - 1);
        Position left = new Position(getX() - 1, getY());
        Position forwardLeft = new Position(getX() - 1, getY() + 1);

        List<Position> possibleMoves = Arrays.asList(forward, forwardRight, right, backRight, back, backLeft, left, forwardLeft);

        for (Position possibleMove : possibleMoves) {
            Piece other = board.getPieceAt(possibleMove);

            if (other != null && other.isEnemyOf(this)) {
                validMoves.add(possibleMove);
            }
            
            if (other == null && !board.isOutOfBounds(possibleMove)) {
                validMoves.add(possibleMove);
            }

            /// TODO: Castling
        }
    
        return validMoves;
    }
}
