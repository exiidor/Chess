package softwareschreiber.chessengine.gamepieces;

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
