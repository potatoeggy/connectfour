// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private String loadBoard; // internal board used for saving and loading 
	private int currentPlayer;
	private int[] players; // actually it means player *types*
	private String[] names;
	private boolean actionLock; // make things feel more responsive

	private boolean verboseHistory; // whether game history should be printed at the end of a game

	/**
	 * Creates a new game panel, with options set by default to allow for simple gameplay out of the box.
	 *
	 * @param eventHandler The event handler that all actions that involve other panels are passed to.
	 * @param verboseHistory	Whether board history should be printed at the end of a game. Mostly used for debugging purposes.
	 */
	public GameWindow(ActionListener eventHandler, boolean verboseHistory) {
		this.verboseHistory = verboseHistory;
		legacyGraphics = false;
		gameOver = false;
		board = new Board();
		loadBoard = "";
		currentPlayer = 1;
		players = new int[2];
		names = new String[] {"Player 1", "Player 2"};
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
		gameStatus.setBackground(Color.RED);
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
						if (!gameOver && players[currentPlayer == 1 ? 0 : 1] == 0) buttonGrid[0][y].setIcon(arrow);
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

	/**
	 * The event handler for all in-game actions.
	 * Includes managing button clickability, displaying of icons, and locking when playing against computers.
	 */
	public void actionPerformed(ActionEvent event) {
		if (actionLock) return;
		int column = (Integer) (((JButton) event.getSource()).getClientProperty("column"));
		loadBoard += column;
		int row = board.addChip(column, currentPlayer); // at least try to add chips
		if (row != -1) {
			buttonGrid[row+1][column].setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW); // unfortunately used to determine if something is occupying the square
			buttonGrid[row+1][column].setText(null); // centre icon
			buttonGrid[row+1][column].setIcon(currentPlayer == 1 ? redPiece : yellowPiece); // give player a pretty piece
			toggleButton(buttonGrid[row+1][column]);
			buttonGrid[row+1][column].setContentAreaFilled(false); // "disable" button

			if (board.checkWin(row, column, currentPlayer)) { // check if a player wins
				endGame(currentPlayer);
			} else if (loadBoard.length() >= 42) { // it's a tie
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

	/**
	 * A utility method to call <code>toggleButton</code> for every button on the screen.
	 * @see	toggleButton
	 */
	public void toggleAllButtons() {
		for (JButton[] array : buttonGrid) {
			for (JButton butt : array) {
				toggleButton(butt);
			}
		}
	}

	/**
	 * Toggles the enabled/disabled state of the button, as well as removes/adds its ActionListener.
	 * If legacy graphics are enabled, the clickability of the button does not change.
	 * @param butt	The JButton to toggle the state of.
	 */
	public void toggleButton(JButton butt) {
		butt.setRolloverEnabled(!butt.isRolloverEnabled());
		if (butt.getActionListeners().length != 0) {
			butt.removeActionListener(this);
		} else {
			butt.addActionListener(this);
		}
		if (!legacyGraphics) butt.setContentAreaFilled(butt.isContentAreaFilled());
	}

	/**
	 * Forces a move by the computer.
	 * This is used when the computer goes first, as the general toggle for a computer move is in <code>actionPerformed</code>
	 */
	public void cpuInit() { // if the computer starts the game
		gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + " is thinking..."); // users don't like not knowing what's happening
		toggleAllButtons();
		toggleLock();
	}

	/**
	 * Ends gameplay and informs players of the winner.
	 * This method provides an indicator to disable saving, changes the status header to indicate the end-of-game state, and disables buttons. 
	 * The <code>gameOver</code> flag is set to true to let other methods know that the game has ended.
	 * @param winningPlayer	An integer to determine what text will be displayed in the header.
	 */
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
		if (verboseHistory) System.out.println(loadBoard);
	}

	/**
	 * Returns whether the game is over.
	 * @return	A boolean flag indicating whether the game has ended.
	 * @see	endGame
	 */
	public boolean isGameOver() {
		return this.gameOver;
	}

	/**
	 * Sets the label to show remaining time for the current player in seconds.
	 * This method does not check to see if the player has lost. If the timer value is invalid, the timer label is cleared.
	 * @param moveTimerRemaining	The remaining time, in seconds, for the current player to move.
	 */
	public void setTimer(int moveTimerRemaining) {
		if (moveTimerRemaining >= 0) { // sanity checking just in case things break in MainWindow (it shouldn't but you never know)
			this.moveTimer.setText("Time: " + moveTimerRemaining);
		} else {
			this.moveTimer.setText(null);
		}
	}

	/**
	 * Sets the names shown in the status header.
	 * If the String array passed contains more than two elements, only the first two will be used.
	 * The status header will be updated immediately with the new name.
	 * @param names	A String array containing the names to be displayed in the status header.
	 */
	public void setNames(String[] names) {
		this.names = names;
		if (currentPlayer == 1) {
			this.gameStatus.setText(names[0] + "'s turn");
		} else {
			this.gameStatus.setText(names[1] + "'s turn");
		}
	}

	/**
	 * Sets the current types of players in the game.
	 * If the integer array passed contains more than two elements, only the first two will be used.
	 * @param players	An int array containing the types (0 for human, 1 for computer) of players that are in the game. 
	 */
	public void setPlayers(int[] players) {
		this.players = players;
	}

	/**
	 * Sets the current board state in the game.
	 * @param board	Any valid Board object.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Returns the current board state in the game.
	 * @return	A Board containing the game state as of the time the method was called.
	 */
	public Board getBoard() {
		return this.board;
	}

	/**
	 * Returns the order of moves that the game has been played through.
	 * @return	A String of integers from 0 to 6, with each integer representing the column that polarity's player placed a piece in.
	 */
	public String getBoardHistory() {
		return this.loadBoard;
	}

	/**
	 * Returns an integer to denote the current player.
	 * @return	1 or -1 to represent player 1 or player 2, respectively.
	 */
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}

	/**
	 * Returns whether the action lock is in place.
	 * The action lock causes all actions that would normally fire events to GameWindow's event handler to be disabled while it is active.
	 * @return	A boolean value representing whether the action lock is activated.
	 */
	public boolean getLock() {
		return this.actionLock;
	}

	/**
	 * Sends a simulated mouse press in the column of the passed integer.
	 * Usually causes the current player to send a chip down that column, if possible.
	 * @param col	The column that the mouse press should be sent to.
	 */
	public void sendClick(int col) {
		buttonGrid[0][col].doClick();
	}

	/**
	 * Enables/disables the action lock.
	 * Toggling this value also toggles the state of the save and quit button. It will be disabled when the action lock is enabled.
	 */
	public void toggleLock() {
		this.actionLock = !this.actionLock;
		this.headerButtons[0].setEnabled(!this.actionLock);
	}

	/**
	 * Loads a game state.
	 * The history of moves is played through and all internal variables are set.
	 * @param loadBoard	A String consisting of only digits, with each number determining a player move in that column.
	 * @param players	An integer array containing the type of player (<code>0</code> or <code>1</code>). If more than two elements are passed in the array, only the first two will be read.
	 * @param names		A String array containing the names of the two players. If more than two elements are passed in the array, only the first two will be read.
	 * @param currentPlayer	An integer denoting the turn player. (<code>1</code> for player 1, <code>2</code> for player 2.)
	 */
	public void loadGame(String loadBoard, int[] players, String[] names, int currentPlayer) {
		this.loadBoard = loadBoard;
		this.names = names;
		this.currentPlayer = currentPlayer;
		for (int i = 0; i < loadBoard.length(); i++) {
			sendClick((int) (loadBoard.charAt(i) - '0')); // convert char holding number to int
		}
		this.players = players;
	}
}