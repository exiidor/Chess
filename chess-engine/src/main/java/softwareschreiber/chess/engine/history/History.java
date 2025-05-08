package softwareschreiber.chess.engine.history;

import java.util.ArrayDeque;
import java.util.Deque;

public class History {
	private final Deque<HistoryEntry> previous = new ArrayDeque<>();
	private final Deque<HistoryEntry> next = new ArrayDeque<>();
	private HistoryEntry current;

	public History() {
		this(HistoryEntry.EMPTY);
	}

	public History(HistoryEntry initial) {
		current = initial;
	}

	public HistoryEntry getCurrent() {
		return current;
	}

	public void push(HistoryEntry value) {
		previous.addLast(current);
		current = value;
		next.clear();
	}

	public void replace(HistoryEntry value) {
		current = value;
	}

	public boolean canGoBack() {
		return !previous.isEmpty();
	}

	public HistoryEntry goBack() {
		next.addFirst(current);
		current = previous.removeLast();
		return current;
	}

	public boolean canGoForward() {
		return !next.isEmpty();
	}

	public HistoryEntry goForward() {
		previous.addLast(current);
		current = next.removeFirst();
		return current;
	}

	@Override
	public String toString() {
		return "History{"
				+ "previous=" + previous
				+ ", next=" + next
				+ ", current=" + current
				+ '}';
	}
}
