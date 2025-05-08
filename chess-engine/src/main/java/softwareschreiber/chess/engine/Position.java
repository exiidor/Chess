package softwareschreiber.chess.engine;

public class Position {
	private final int x;
	private final int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean equals(Position position) {
		return x == position.getX() && y == position.getY();
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
