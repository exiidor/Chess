package softwareschreiber.chess.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.player.HumanPlayer;

public class GuiPlayer extends HumanPlayer {
	public GuiPlayer(PieceColor pieceColor) {
		super(pieceColor);
	}

	@Override
	public Move chooseMove(Board board) {
		throw new IllegalStateException();
	}

	@Override
	public PromotionMove choosePromotionMove(Board board, Pawn pawn, Set<PromotionMove> moves) {
		Map<String, PromotionMove> movesByName = new HashMap<>();

		for (PromotionMove move : moves) {
			movesByName.put(move.getReplacement().getName(), move);
		}

		String[] options = movesByName.keySet().toArray(String[]::new);

		while (true) {
			int choiceIndex = JOptionPane.showOptionDialog(
					null,
					"Choose piece to promote to:",
					"Pawn Promotion",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE,
					null, options, options[0]);

			if (choiceIndex == -1) {
				// User cancelled the dialog
			} else {
				String choice = options[choiceIndex];
				return movesByName.get(choice);
			}
		}
	}
}
