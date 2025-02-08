package softwareschreiber.chessengine;

public class IllegalMoveException extends Exception {
	IllegalMoveException() {
		super("Diese Nachricht ist eine Test");
	}

	IllegalMoveException(String error) {
		super(error);
	}
}
