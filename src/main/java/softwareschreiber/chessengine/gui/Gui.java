package softwareschreiber.chessengine.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Move;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;
import softwareschreiber.chessengine.util.Util;

public class Gui {
	private Board board;
	private ChessPanel[][] squares = new ChessPanel[8][8];
	private List<ChessPanel> highlightedSquares = new ArrayList<>();
	private Piece selectedPiece;

	public Gui() {
		JFrame frame = new JFrame("Software-Schreiber Schach");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		board = new Board() {
			@Override
			protected Piece promote(Pawn piece) {
				String[] options = {"Queen", "Rook", "Bishop", "Knight"};
				int choice = JOptionPane.showOptionDialog(
						null,
						"Choose piece to promote to:",
						"Pawn Promotion",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE,
						null, options, options[0]);

				switch (choice) {
					case 0:
						return new Queen(piece.isWhite(), this);
					case 1:
						return new Rook(piece.isWhite(), this);
					case 2:
						return new Bishop(piece.isWhite(), this);
					case 3:
						return new Knight(piece.isWhite(), this);
					default:
						return new Queen(piece.isWhite(), this);
				}
			}
		};
		board.initializeStartingPositions();

		JPanel panel = new JPanel(new GridLayout(8, 8));
		boolean white = true;

		for (int y = board.getMinY(); y <= board.getMaxY(); y++) {
			for (int x = board.getMinX(); x <= board.getMaxX(); x++) {
				ChessPanel square = new ChessPanel(new JPanel(), white ? Color.WHITE : new Color(36, 36, 36));
				squares[y][x] = square;
				Piece piece = board.getPieceAt(x, y);

				if (piece != null) {
					char firstLetterOfFileName = (piece.getName() + piece.isWhite()).toUpperCase().charAt(0);
					String restOfFileName = (piece.getName() + piece.isWhite()).substring(1).toLowerCase();
					String nameOfFile = (firstLetterOfFileName + restOfFileName).trim();
					square.setPicture(new ImageIcon(Util.getResource("/graphics/" + nameOfFile + ".png").toString()));
				}

				square.getUI().addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						clearHighlightedSquares();

						if (piece == null || piece == selectedPiece) {
							selectedPiece = null;
							return;
						}

						square.setColor(Color.RED);
						highlightedSquares.add(square);
						showPossibleMoves(piece);
						selectedPiece = piece;
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}
				});

				panel.add(square.getUI());
				white = !white;
			}

			white = !white;
		}

		frame.add(panel);

		frame.setSize(1024, 1024);
		frame.setVisible(true);
	}

	private void showPossibleMoves(Piece piece) {
		for (Move move : piece.getValidMoves()) {
			Position position = move.getTargetPos();
			ChessPanel square = squares[position.getY()][position.getX()];
			square.setColor(Color.GREEN);
			highlightedSquares.add(square);
		}
	}

	private void clearHighlightedSquares() {
		for (ChessPanel square : highlightedSquares) {
			square.resetColor();
		}

		highlightedSquares.clear();
	}

	public static void main(String[] args) {
		new Gui();
	}
}
