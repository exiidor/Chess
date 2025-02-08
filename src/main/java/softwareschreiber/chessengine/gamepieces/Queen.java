package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;

public class Queen extends Piece {
	public Queen(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public char getSymbol() {
		return 'Q';
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

		for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
			for (int xDirection = -1; xDirection <= 1; xDirection += 2) {
				for (int i = 1; i < 8; i++) {
					Position position = new Position(getX() + i * xDirection, getY() + i * yDirection);

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
		}

		return validMoves;
	}
}
