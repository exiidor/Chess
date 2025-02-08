package softwareschreiber.chessengine.gamepieces;

import java.util.LinkedHashSet;
import java.util.Set;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Piece;
import softwareschreiber.chessengine.Position;

public class Queen extends Piece {
	public Queen(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public String getName() {
		return "Queen";
	}

	@Override
	public Set<? extends Move> getValidMoves() {
		Set<Move> validMoves = new LinkedHashSet<>();

		for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
			for (int i = 1; i < 8; i++) {
				Position position = new Position(getX(), getY() + i * yDirection);
				Move move = new Move(getPosition(), position);

				if (board.isOutOfBounds(position)) {
					break;
				}

				Piece other = board.getPieceAt(position);

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
				Position position = new Position(getX() + i * xDirection, getY());

				if (board.isOutOfBounds(position)) {
					break;
				}

				Piece other = board.getPieceAt(position);
				Move move = new Move(getPosition(), position);

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

		for (int yDirection = -1; yDirection <= 1; yDirection += 2) {
			for (int xDirection = -1; xDirection <= 1; xDirection += 2) {
				for (int i = 1; i < 8; i++) {
					Position position = new Position(getX() + i * xDirection, getY() + i * yDirection);

					if (board.isOutOfBounds(position)) {
						break;
					}

					Piece other = board.getPieceAt(position);
					Move move = new Move(getPosition(), position);

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
		}

		return validMoves;
	}
}
