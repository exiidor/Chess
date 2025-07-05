package softwareschreiber.chess.gui;

import javax.swing.JOptionPane;

import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.player.ComputerPlayer;

public class GuiGame extends Game {
	public GuiGame(PieceColor startingColor) {
		super((color, game) -> new GuiPlayer(color),
				(color, game) -> new ComputerPlayer(color));
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
