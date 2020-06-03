// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameWindow extends JPanel implements ActionListener {
	private final JPanel header;
	private final JPanel body; // two panels for main game and status
	private final JPanel[] headerJustification; // align things left, centre, and right
	JButton[] headerButtons; // new game, save and quit
	JButton optionsButton; // options
	private final JLabel gameStatus;
	private final JLabel moveTimer; // status and timer, if enabled
	private final JButton[][] buttonGrid; // connect 4 grid
	private ImageIcon arrow, redPiece, yellowPiece; // graphics
	private boolean legacyGraphics;
	private boolean gameOver;

	// internal game variables copied from MainWindow
	private Board board; // internal board
	private int currentPlayer, cpuDifficulty;
	private int[] players; // actually it means player *types*
	private String[] names;
	private int internalTurnCount;
	private int buttonsFilled; // when at top the game is a tie
	private boolean actionLock; // make things feel more responsive

	public GameWindow(ActionListener eventHandler) {
		legacyGraphics = false;
		gameOver = false;
		board = new Board();
		currentPlayer = 1;
		cpuDifficulty = 0;
		players = new int[2];
		names = new String[] {"Player 1", "Player 2"};
		internalTurnCount = 0;
		buttonsFilled = 0;
		actionLock = false;

		header = new JPanel();
		headerJustification = new JPanel[]{new JPanel(), new JPanel(), new JPanel()};
		body = new JPanel();
		headerButtons = new JButton[]{new JButton("Save & quit"), new JButton("New game")};
		optionsButton = new JButton("Options");
		buttonGrid = new JButton[7][7];
		gameStatus = new JLabel(names[0] + "'s turn");
		moveTimer = new JLabel();

		gameStatus.setFont(new Font("sans-serif", Font.BOLD, 18)); // make it big and bold
		gameStatus.setBackground(Color.RED); // TODO: remember to handle colour backgrounds when loading saves
		gameStatus.setOpaque(true); // support filling in backgrounds

		try { // try to load fancy pictures
			arrow = new ImageIcon(ImageIO.read(getClass().getResource("resources/arrow.png")));
			redPiece = new ImageIcon(ImageIO.read(getClass().getResource("resources/red.png")));
			yellowPiece = new ImageIcon(ImageIO.read(getClass().getResource("resources/yellow.png")));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("One or more necessary resources were not found. Falling back to legacy graphics.");
			arrow = new ImageIcon(); // just in case to prevent null pointers
			redPiece = new ImageIcon();
			yellowPiece = new ImageIcon();
			legacyGraphics = true; // just set their background then
		}

		for (JButton butt : headerButtons) {
			headerJustification[0].add(butt); // justify left
			butt.addActionListener(eventHandler);
		}

		headerJustification[1].add(gameStatus); // justify centre
		headerJustification[2].add(optionsButton); // justify right
		optionsButton.addActionListener(eventHandler);
		headerJustification[2].add(moveTimer); // justify right

		for (JPanel pan : headerJustification) { // fill in the header
			header.add(pan);
		}
		headerJustification[0].setLayout(new FlowLayout(FlowLayout.LEFT)); // the layout justifies things
		headerJustification[1].setLayout(new FlowLayout(FlowLayout.CENTER));
		headerJustification[2].setLayout(new FlowLayout(FlowLayout.RIGHT));

		for (int i = 0; i < buttonGrid.length; i++) { // fill in the button arrays
			for (int j = 0; j < buttonGrid[i].length; j++) {
				buttonGrid[i][j] = new JButton("<html><br><br><br><br><br><br></html>"); // make multi-line buttons
				buttonGrid[i][j].addActionListener(this);
				buttonGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // create a grid
				buttonGrid[i][j].putClientProperty("column", j); // store coords
				buttonGrid[i][j].setBackground(Color.WHITE); // make pretty
				buttonGrid[i][j].setFocusPainted(false); // do not have ugly lines
				buttonGrid[i][j].addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent event) { // highlight column when mouse goes over things
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("column"));
						if (!gameOver) buttonGrid[0][y].setIcon(arrow);
					}

					public void mouseExited(MouseEvent event) { // get rid of highlight when mouse leaves
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("column"));
						buttonGrid[0][y].setIcon(null);
					}
				});
				body.add(buttonGrid[i][j]);
			}
		}

		for (int i = 0; i < buttonGrid[0].length; i++) {
			buttonGrid[0][i].setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE)); // make top open-ended
			buttonGrid[0][i].setText(null);
		}

		add(header);
		add(body);
		header.setLayout(new GridLayout(1, 3));
		body.setLayout(new GridLayout(7, 7, -1, -1));
		body.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2)); // make margins
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setVisible(false);
	}

	public void actionPerformed(ActionEvent event) {
		if (actionLock) return;
		int column = (Integer) (((JButton) event.getSource()).getClientProperty("column"));
		int row = board.addChip(column, currentPlayer); // at least try to add chips
		if (row != -1) {
			buttonGrid[row+1][column].setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW); // unfortunately used to determine if something is occupying the square
			buttonGrid[row+1][column].setText(null); // centre icon
			buttonGrid[row+1][column].setIcon(currentPlayer == 1 ? redPiece : yellowPiece); // give player a pretty piece
			toggleButton(buttonGrid[row+1][column]);
			buttonGrid[row+1][column].setContentAreaFilled(false); // "disable" button
			buttonsFilled++;

			internalTurnCount++;
			if (board.checkWin(row, column, currentPlayer)) { // check if a player wins
				endGame(currentPlayer);
			} else if (buttonsFilled >= 42) { // it's a tie
				endGame(0);
			} else {
				currentPlayer = ((currentPlayer - 1) ^ 1) + 1; // switch player
				gameStatus.setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW); // give visual indication of turn
				if (players[currentPlayer == 1 ? 0 : 1] == 1) { // if player is a computer
					gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + " is thinking..."); // users don't like not knowing what's happening
					toggleAllButtons();
					toggleLock();
				} else {
					gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + "'s turn"); // update header
				}
			}
		} else if (players[currentPlayer == 1 ? 0 : 1] == 1) { // we should only hit here if recalculating for random or if ai is broken
			// recalculate
			toggleAllButtons();
			toggleLock();
		}
	}

	public void toggleAllButtons() {
		for (JButton[] array : buttonGrid) {
			for (JButton butt : array) {
				toggleButton(butt);
			}
		}
	}

	public void toggleButton(JButton butt) {
		butt.setRolloverEnabled(!butt.isRolloverEnabled());
		if (butt.getActionListeners().length != 0) {
			butt.removeActionListener(this);
		} else {
			butt.addActionListener(this);
		}
		if (!legacyGraphics) butt.setContentAreaFilled(butt.getBackground() == Color.WHITE);
	}

	public void cpuInit() { // if the computer starts the game
		gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + " is thinking..."); // users don't like not knowing what's happening
		toggleAllButtons();
		toggleLock();
	}

	public void endGame(int winningPlayer) { // handles game ending procedures
		toggleAllButtons();
		headerButtons[0].setText("Quit"); // do not save when game is over
		if (winningPlayer == 0) {
			gameStatus.setText("It's a draw!");
		} else {
			gameStatus.setText((winningPlayer == 1 ? names[0] : names[1]) + " wins!"); // display whoever wins
		}
		gameStatus.setBackground(null); // remove colour because it's no-one's turn
		gameOver = true;
	}

	public boolean isGameOver() {
		return this.gameOver;
	}

	public void setDifficulty(int cpuDifficulty) {
		this.cpuDifficulty = cpuDifficulty;
	}

	public void setTimer(int moveTimerRemaining) {
		if (moveTimerRemaining >= 0) { // sanity checking just in case things break in MainWindow (it shouldn't but you never know)
			this.moveTimer.setText("Time: " + moveTimerRemaining);
		} else {
			this.moveTimer.setText(null);
		}
	}

	public void setNames(String[] names) {
		this.names = names;
		if (currentPlayer == 1) this.gameStatus.setText(names[0] + "'s turn");
	}

	public void setPlayers(int[] players) {
		this.players = players;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Board getBoard() {
		return this.board;
	}

	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	public int getTurnCount() {
		return this.internalTurnCount;
	}

	public int getDifficulty() {
		return this.cpuDifficulty;
	}

	public boolean getLock() {
		return this.actionLock;
	}

	public void sendClick(int col) {
		buttonGrid[0][col].doClick();
	}

	public void toggleLock() {
		this.actionLock = !this.actionLock;
		this.headerButtons[0].setEnabled(!this.actionLock);
	}
}