package softwareschreiber.chess.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.PromotionMove;

public class GuiGame extends Game {
	public GuiGame(PieceColor startingColor) {
		super(startingColor);
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

	@Override
	public void checkMate(PieceColor winningColor) {
		JOptionPane.showMessageDialog(
				null,
				winningColor.getOpposite() + " is in checkmate. Game over.",
				"Checkmate",
				JOptionPane.INFORMATION_MESSAGE);
		endGame();
	}

	@Override
	public void staleMate() {
		JOptionPane.showMessageDialog(
				null,
				"Stalemate. Game over.",
				"Stalemate",
				JOptionPane.INFORMATION_MESSAGE);
		endGame();
	}
}
