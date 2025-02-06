package main.java.gamelogic.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import main.java.gamelogic.Board;
import main.java.gamelogic.Piece;
import main.java.gamelogic.Position;

public class Rook extends Piece {
    public Rook(boolean isWhite, Board board) {
        super(isWhite, board);
    }

    @Override
    public char getSymbol() {
        return 'R';
    }

    @Override
    public Set<Position> getValidMoves() {
        Set<Position> validMoves = new LinkedHashSet<>();

        for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
            for (int i = 1; i < 8; i++) {
                Position position = new Position(getX(), getY() + i * yDirection);

                if (board.isOutOfBounds(position)) {
                    break;
                }

                Piece other = board.getPieceAt(position);

                if (other == null) {
                    validMoves.add(position);
                } else if (other != null) {
                    if (other.isEnemyOf(this)) {
                        validMoves.add(position);
                    }
                    
                    break;
                }
            }
        }

        for (int xDirection = -1; xDirection <= 1; xDirection += 2) {
            for (int i = 1; i < 8; i++) {
                Position position = new Position(getX() + i * xDirection, getY());

                if (board.isOutOfBounds(position)) {
                    break;
                }

                Piece other = board.getPieceAt(position);

                if (other == null) {
                    validMoves.add(position);
                } else if (other != null) {
                    if (other.isEnemyOf(this)) {
                        validMoves.add(position);
                    }
                    
                    break;
                }
            }
        }

        /*  
        TODO: Castling
        Castling : boolean hasMoved
        */
        return validMoves;
    }
}
