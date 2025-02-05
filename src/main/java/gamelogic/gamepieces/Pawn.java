package main.java.gamelogic.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import main.java.gamelogic.Board;
import main.java.gamelogic.Piece;
import main.java.gamelogic.Position;

public class Pawn extends Piece {
    public Pawn(boolean isWhite, Board board) {
        super(isWhite, board);
    }

    @Override
    public char getSymbol() {
        return 'P';
    }

    private int getDirection() {
        return isWhite() ? 1 : -1;
    }

    @Override
    public Set<Position> getValidMoves() {
        Set<Position> moves = new LinkedHashSet<>();
        boolean canMoveTwo;

        if (isWhite()) {
            canMoveTwo = getY() == 1;
        } else {
            canMoveTwo = getY() == 6;
        }

        Position forward = new Position(getX(), getY() + getDirection());
        Position forwardByTwo = new Position(getX(), getY() + getDirection() * 2);

        if (board.getPieceAt(forward) == null) {
            moves.add(forward);

            if (canMoveTwo && board.getPieceAt(forwardByTwo) == null) {
                moves.add(forwardByTwo);
            }
        }

        Position forwardLeft = new Position(getX() - 1, getY() + getDirection());
        Position forwardRight = new Position(getX() + 1, getY() + getDirection());

        Piece forwardLeftPiece = board.getPieceAt(forwardLeft);
        Piece forwardRightPiece = board.getPieceAt(forwardRight);

        if (forwardLeftPiece != null && forwardLeftPiece.isEnemyOf(this)) {
            moves.add(forwardLeft);
        }

        if (forwardRightPiece != null && forwardRightPiece.isEnemyOf(this)) {
            moves.add(forwardRight);
        }

        // TODO: En passant & Promotion

        return moves;
    }
}
