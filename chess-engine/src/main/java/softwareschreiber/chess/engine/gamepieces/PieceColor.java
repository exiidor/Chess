package softwareschreiber.chess.engine.gamepieces;

/**
 * The valid colors of a chess piece.
 */
public enum PieceColor {
	WHITE,
	BLACK;

	public PieceColor getOpposite() {
		if (this == WHITE) {
			return BLACK;
		} else {
			return WHITE;
		}
	}
}
