package softwareschreiber.chessengine.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import softwareschreiber.chessengine.Board;
import softwareschreiber.chessengine.Position;
import softwareschreiber.chessengine.gamepieces.Bishop;
import softwareschreiber.chessengine.gamepieces.Knight;
import softwareschreiber.chessengine.gamepieces.Pawn;
import softwareschreiber.chessengine.gamepieces.Piece;
import softwareschreiber.chessengine.gamepieces.Queen;
import softwareschreiber.chessengine.gamepieces.Rook;
import softwareschreiber.chessengine.move.CaptureMove;
import softwareschreiber.chessengine.move.Move;
import softwareschreiber.chessengine.util.Util;

public class Gui {
	private static final Color BLUE = new Color(120, 200, 250);
	private static final Color RED = new Color(240, 105, 110);
	private static final Color GRAY = new Color(112, 112, 112);
	private static final Color BACKGROUND_BLACK = new Color(36, 36, 36);
	private final Board board;
	private final ChessPanel[][] squares;
	private final List<ChessPanel> highlightedSquares;
	private final Map<ChessPanel, Move> highlightedSquareMoves;
	private Piece selectedPiece;

	public Gui() {
		squares = new ChessPanel[8][8];
		highlightedSquares = new ArrayList<>();
		highlightedSquareMoves = new HashMap<>();

		JFrame frame = new JFrame("Software-Schreiber Schach");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		board = new Board() {
			@Override
			protected Piece getPromotionTarget(Pawn piece) {
				String[] options = { "Queen", "Rook", "Bishop", "Knight" };
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
						return getPromotionTarget(piece);
				}
			}
		};
		board.initializeStartingPositions();

		JPanel panel = new JPanel(new GridLayout(8, 8));
		boolean white = true;

		for (int y = board.getMaxY(); y >= board.getMinY(); y--) {
			for (int x = board.getMinX(); x <= board.getMaxX(); x++) {
				ChessPanel square = new ChessPanel(new JPanel(), white ? Color.WHITE : BACKGROUND_BLACK);
				squares[y][x] = square;
				Piece piece = board.getPieceAt(x, y);

				if (piece != null) {
					square.setPiece(piece);
				}

				square.getUI().addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent e) {
						Piece piece = square.getPiece();

						if (highlightedSquares.contains(square) && selectedPiece != piece) {
							board.move(selectedPiece, highlightedSquareMoves.get(square));
							clearHighlightedSquares();
							return;
						}

						clearHighlightedSquares();

						if (piece == null || piece == selectedPiece) {
							selectedPiece = null;
							return;
						}

						square.setColor(GRAY);
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

		board.addPieceMovedListener((piece, move) -> {
			Position sourcePos = move.getSourcePos();
			Position targetPos = move.getTargetPos();
			ChessPanel startSquare = squares[sourcePos.getY()][sourcePos.getX()];
			ChessPanel targetSquare = squares[targetPos.getY()][targetPos.getX()];

			startSquare.setPiece(null);
			targetSquare.setPiece(piece);
		});

		board.addMoveUndoneListener((piece, move) -> {
			Position sourcePos = move.getSourcePos();
			Position targetPos = move.getTargetPos();
			ChessPanel startSquare = squares[sourcePos.getY()][sourcePos.getX()];
			ChessPanel targetSquare = squares[targetPos.getY()][targetPos.getX()];

			startSquare.setPiece(piece);
			targetSquare.setPiece(null);
		});

		frame.add(panel);
		frame.setSize(1024, 1024);
		frame.setVisible(true);
	}

	private void showPossibleMoves(Piece piece) {
		for (Move move : piece.getValidMoves(true)) {
			Position position = move.getTargetPos();
			ChessPanel square = squares[position.getY()][position.getX()];
			highlightedSquareMoves.put(square, move);

			if (move instanceof CaptureMove) {
				square.setColor(RED); // Red
			} else {
				square.setColor(BLUE); // Blue
			}

			highlightedSquares.add(square);
		}
	}

	private void clearHighlightedSquares() {
		for (ChessPanel square : highlightedSquares) {
			square.resetColor();
		}

		highlightedSquares.clear();
		highlightedSquareMoves.clear();
	}

	static ImageIcon getImageForPiece(Piece piece) {
		char firstLetterOfFileName = (piece.getName() + piece.isWhite()).toUpperCase().charAt(0);
		String restOfFileName = (piece.getName() + piece.isWhite()).substring(1).toLowerCase();
		String nameOfFile = (firstLetterOfFileName + restOfFileName).trim();
		return new ImageIcon(Util.getResource("/graphics/" + nameOfFile + ".png").toString());
	}

	public static void main(String[] args) {
		new Gui();
	}
}
