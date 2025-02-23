package softwareschreiber.chessengine.evaluation;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.PieceColor;

public class Evaluation {
	private Board board;

	public Evaluation(Board board) {
		this.board = board;
	}

	@Deprecated
	public int pieceCountEvaluate() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += piece.getValue();
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.getValue();
		}

		return score;
	}

	public int chartEvaluate() {
		int score = 0;

		for (Piece piece : board.getPieces(PieceColor.WHITE)) {
			score += piece.evaluationChart()[piece.getX()][piece.getY()];
		}

		for (Piece piece : board.getPieces(PieceColor.BLACK)) {
			score -= piece.evaluationChart()[piece.getX()][piece.getY()];
		}

		return score;
	}
}
