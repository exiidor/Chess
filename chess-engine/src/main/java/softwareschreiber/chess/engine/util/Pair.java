package softwareschreiber.chess.engine.util;

import java.util.Objects;

public class Pair<A, B> {
	private final A a;
	private final B b;

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<>(a, b);
	}

	private Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getLeft() {
		return a;
	}

	public B getRight() {
		return b;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(a) * 31 + Objects.hashCode(b);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Pair && Objects.equals(a, ((Pair<?, ?>) o).a) && Objects.equals(b, ((Pair<?, ?>) o).b);
	}

	@Override
	public String toString() {
		return "(" + a + ", " + b + ")";
	}
}
