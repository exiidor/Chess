package softwareschreiber.chess.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import softwareschreiber.chess.engine.Board;
import softwareschreiber.chess.engine.Game;
import softwareschreiber.chess.engine.Position;
import softwareschreiber.chess.engine.gamepieces.Pawn;
import softwareschreiber.chess.engine.gamepieces.Piece;
import softwareschreiber.chess.engine.gamepieces.PieceColor;
import softwareschreiber.chess.engine.move.CaptureMove;
import softwareschreiber.chess.engine.move.Move;
import softwareschreiber.chess.engine.move.PromotionMove;
import softwareschreiber.chess.engine.util.Util;

public class Gui {
	private static final Color BLUE = new Color(120, 200, 250);
	private static final Color DIM_BLUE = new Color(40, 70, 90);
	private static final Color RED = new Color(240, 105, 110);
	private static final Color DIM_RED = new Color(80, 40, 40);
	private static final Color GRAY = new Color(112, 112, 112);
	private static final Color GOLD = new Color(252, 197, 45);
	private static final Color DIM_GOLD = new Color(80, 70, 15);
	private static final Color BACKGROUND_BLACK = new Color(36, 36, 36);
	private final JFrame windowFrame;
	private final JPanel squareContainer;
	private final List<ChessPanel> highlightedSquares;
	private final Map<ChessPanel, Set<Move>> highlightedSquareMoves;
	private ChessPanel[][] squares;
	private GuiGame game;
	private Board board;
	private Piece selectedPiece;
	private Duration timeTaken;

	public Gui() {
		highlightedSquares = new ArrayList<>();
		highlightedSquareMoves = new HashMap<>();

		windowFrame = new JFrame();
		windowFrame.setResizable(false);
		windowFrame.setIconImage(new ImageIcon(Util.getResource("/graphics/PawnBLACK.png").toString()).getImage());
		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		squareContainer = new JPanel();
		windowFrame.add(squareContainer);

		windowFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_U -> {
						getBoard().undo(false);
						getBoard().undo(false);
					}
					case KeyEvent.VK_R -> reloadSquares();
					case KeyEvent.VK_N -> initGame();
					case KeyEvent.VK_L -> game.checkMate(PieceColor.BLACK);
					case KeyEvent.VK_W -> game.checkMate(PieceColor.WHITE);
				}
			}
		});

		initGame();

		windowFrame.setSize(1024, 1024);
		windowFrame.setVisible(true);
	}

	private Board getBoard() {
		return board;
	}

	public Game getGame() {
		return game;
	}

	private void initGame() {
		game = new GuiGame(PieceColor.WHITE);
		board = game.getBoard();
		game.startGame();

		squareContainer.removeAll();
		squareContainer.setLayout(new GridLayout(board.getMaxY() + 1, board.getMaxX() + 1));

		squares = new ChessPanel[board.getMaxY() + 1][board.getMaxX() + 1];
		highlightedSquares.clear();
		highlightedSquareMoves.clear();

		board.addPieceMovedListener(this::onPieceMoved);
		board.addSubmittedMoveDoneListener(this::onSubmittedMoveDone);
		board.addMoveUndoneListener(this::onPieceMoveUndone);
		board.addSubmittedUndoMoveDoneListener(this::onSubmittedUndoMoveDone);
		game.addGameEndListener(this::onGameEnd);

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
					@SuppressWarnings("unchecked")
					@Override
					public void mouseClicked(MouseEvent e) {
						Piece piece = square.getPiece();

						if (highlightedSquares.contains(square) && selectedPiece != piece) {
							if (game.isTimeForTurn(selectedPiece)) {
								Set<? extends Move> moves = highlightedSquareMoves.get(square);
								Move move;

								if (moves.size() == 1) {
									move = moves.iterator().next();
								} else {
									move = game.getPlayer(selectedPiece.getColor()).choosePromotionMove(getBoard(), (Pawn) selectedPiece, (Set<PromotionMove>) moves);
								}

								getBoard().move(selectedPiece, move, game.getWhitePlayer());
							}

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

				squareContainer.add(square.getUI());
				white = !white;
			}

			white = !white;
		}

		squareContainer.revalidate();
		squareContainer.repaint();
		updateTitle();
	}

	public void reloadSquares() {
		clearHighlightedSquares();

		for (int y = board.getMaxY(); y >= board.getMinY(); y--) {
			for (int x = board.getMinX(); x <= board.getMaxX(); x++) {
				Piece piece = board.getPieceAt(x, y);
				ChessPanel square = getSquareAt(x, y);

				square.setPiece(piece);
			}
		}
	}

	private ChessPanel getSquareAt(Position position) {
		return getSquareAt(position.x(), position.y());
	}

	private ChessPanel getSquareAt(int x, int y) {
		return squares[y][x];
	}

	private void updateTitle() {
		String title = "Software-Schreiber Schach";
		String eval = String.valueOf(board.evaluate());

		if (game.isGameOver()) {
			if (game.isWhitesTurn()) {
				title += " - White won";
			} else {
				title += " - Black won";
			}
		} else {
			if (game.isWhitesTurn()) {
				title += " - White's turn";
			} else {
				title += " - Black's turn";
			}

			title += " - Evaluation: " + eval;

			if (timeTaken != null) {
				title += " - Last move took " + timeTaken.toMillis() + " ms";
			}
		}

		windowFrame.setTitle(title);
	}

	private void onPieceMoved(Piece piece, Move move) {
		Position sourcePos = move.getSourcePos();
		Position targetPos = move.getTargetPos();
		ChessPanel sourceSquare = getSquareAt(sourcePos);
		ChessPanel targetSquare = getSquareAt(targetPos);

		if (move instanceof CaptureMove captureMove && !(move instanceof PromotionMove)) {
			getSquareAt(captureMove.getCaptured().getPosition()).setPiece(null);
		}

		sourceSquare.setPiece(null);

		if (move instanceof PromotionMove promotionMove) {
			if (promotionMove.getCaptured() != null) {
				getSquareAt(promotionMove.getCaptured().getPosition()).setPiece(null);
			}

			targetSquare.setPiece(promotionMove.getReplacement());
		} else {
			targetSquare.setPiece(piece);
		}
	}

	private void onSubmittedMoveDone(Piece piece, Move move) {
		updateTitle();

		if (piece.isBlack()) {
			return;
		}

		SwingUtilities.invokeLater(() -> {
			Instant before = Instant.now();
			Move chosenMove = game.getBlackPlayer().chooseMove(board);
			Position sourcePos = chosenMove.getSourcePos();
			Piece pieceToMove = board.getPieceAt(sourcePos);
			Instant after = Instant.now();

			board.move(pieceToMove, chosenMove, game.getBlackPlayer());
			game.setIsWhitesTurn(true);
			timeTaken = Duration.between(before, after);
			updateTitle();
		});
	}

	private void onPieceMoveUndone(Piece piece, Move move) {
		Position sourcePos = move.getSourcePos();
		Position targetPos = move.getTargetPos();
		ChessPanel sourceSquare = getSquareAt(sourcePos);
		ChessPanel targetSquare = getSquareAt(targetPos);

		sourceSquare.setPiece(piece);
		targetSquare.setPiece(null);

		if (move instanceof CaptureMove captureMove && !(move instanceof PromotionMove)) {
			getSquareAt(captureMove.getCaptured().getPosition()).setPiece(captureMove.getCaptured());
		} else if (move instanceof PromotionMove promotionMove) {
			if (promotionMove.getCaptured() != null) {
				getSquareAt(promotionMove.getCaptured().getPosition()).setPiece(promotionMove.getCaptured());
			}
		}
	}

	private void onSubmittedUndoMoveDone(Piece piece, Move move) {
		updateTitle();
	}

	private void onGameEnd(PieceColor winningColor) {
		initGame();
	}

	private void showPossibleMoves(Piece piece) {
		for (Move move : piece.getSafeMoves()) {
			Position position = move.getTargetPos();
			ChessPanel square = squares[position.y()][position.x()];
			highlightedSquareMoves.computeIfAbsent(square, unused -> new HashSet<>()).add(move);
			boolean isPiecesTurn = game.isTimeForTurn(piece);

			if (move instanceof CaptureMove && !(move instanceof PromotionMove)) {
				square.setColor(isPiecesTurn ? RED : DIM_RED);
			} else if (move instanceof PromotionMove) {
				square.setColor(isPiecesTurn ? GOLD : DIM_GOLD);
			} else {
				square.setColor(isPiecesTurn ? BLUE : DIM_BLUE);
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
		char firstLetterOfFileName = piece.getName().toUpperCase().charAt(0);
		String restOfFileName = (piece.getName() + piece.getColor()).substring(1);
		String nameOfFile = (firstLetterOfFileName + restOfFileName).trim();
		return new ImageIcon(Util.getResource("/graphics/" + nameOfFile + ".png").toString());
	}
}
