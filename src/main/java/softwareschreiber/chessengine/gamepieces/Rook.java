package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;

public class Rook extends Piece {
	public Rook(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public char getSymbol() {
		return 'R';
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
			for (int i = 1; i < 8; i++) {
				Position targetPos = new Position(getX(), getY() + i * yDirection);

				if (board.isOutOfBounds(targetPos)) {
					break;
				}

				Piece other = board.getPieceAt(targetPos);
				Move move = new Move(getPosition(), targetPos);

				if (other == null) {
					validMoves.add(move);
				} else if (other != null) {
					if (other.isEnemyOf(this)) {
						validMoves.add(move);
					}

					break;
				}
			}
		}

		for (int xDirection = -1; xDirection <= 1; xDirection += 2) {
			for (int i = 1; i < 8; i++) {
				Position targetPos = new Position(getX() + i * xDirection, getY());

				if (board.isOutOfBounds(targetPos)) {
					break;
				}

				Piece other = board.getPieceAt(targetPos);
				Move move = new Move(getPosition(), targetPos);

				if (other == null) {
					validMoves.add(move);
				} else if (other != null) {
					if (other.isEnemyOf(this)) {
						validMoves.add(move);
					}

					break;
				}
			}
		}

		/*
		 * TODO: Castling
		 * Castling : boolean hasMoved
		 */
		return validMoves;
	}
}
