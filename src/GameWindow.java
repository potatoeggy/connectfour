// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;

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
	private Board board;
	private int currentPlayer, cpuDifficulty;
	private int[] players;
	private String[] names;
	private int buttonsFilled; // when at top the game is a tie

	public GameWindow(ActionListener eventHandler) {
		legacyGraphics = false;
		gameOver = false;
		board = new Board();
		currentPlayer = 1;
		cpuDifficulty = 0;
		players = new int[2];
		names = new String[] {"Player 1", "Player 2"};
		buttonsFilled = 0;

		header = new JPanel();
		headerJustification = new JPanel[]{new JPanel(), new JPanel(), new JPanel()};
		body = new JPanel();
		headerButtons = new JButton[]{new JButton("Save & quit"), new JButton("New game")};
		optionsButton = new JButton("Options");
		buttonGrid = new JButton[7][7];
		gameStatus = new JLabel(names[0] + "'s turn");
		moveTimer = new JLabel();

		gameStatus.setFont(new Font("sans-serif", Font.BOLD, 18));
		gameStatus.setBackground(Color.RED); // TODO: remember to handle colour backgrounds when loading saves
		gameStatus.setOpaque(true);

		try {
			arrow = new ImageIcon(ImageIO.read(getClass().getResource("resources/arrow.png")));
			redPiece = new ImageIcon(ImageIO.read(getClass().getResource("resources/red.png")));
			yellowPiece = new ImageIcon(ImageIO.read(getClass().getResource("resources/yellow.png")));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("One or more necessary resources were not found. Falling back to legacy graphics.");
			legacyGraphics = true;
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
				buttonGrid[i][j].putClientProperty("x", i); // store coordinates
				buttonGrid[i][j].putClientProperty("y", j);
				buttonGrid[i][j].setBackground(Color.WHITE); // make pretty
				buttonGrid[i][j].setFocusPainted(false); // do not have ugly lines
				buttonGrid[i][j].addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent event) { // highlight column when mouse goes over things
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						if (!gameOver) buttonGrid[0][y].setIcon(arrow);
					}

					public void mouseExited(MouseEvent event) { // get rid of highlight when mouse leaves
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						if (!gameOver) buttonGrid[0][y].setIcon(null);
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
		body.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setVisible(false);
	}

	public void actionPerformed(ActionEvent event) {
		if (gameOver) return;
		int column = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
		int row = board.addChip(column, currentPlayer);
		if (row != -1) {
			buttonGrid[row+1][column].setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW); // unfortunately used to determine if something is occupying the square
			buttonGrid[row+1][column].setText(null); // centre icon
			buttonGrid[row+1][column].setIcon(currentPlayer == 1 ? redPiece : yellowPiece); // make it known to user (no idea how to force square gridlayouts)
			buttonGrid[row+1][column].setRolloverEnabled(false);
			if (!legacyGraphics) buttonGrid[row+1][column].setContentAreaFilled(false); // "disable" button because setEnabled is garbage and makes everything gray
			buttonGrid[row+1][column].removeActionListener(this);
			buttonsFilled++;

			if (board.checkWin(row, column, currentPlayer)) { // check if a player wins
				endGame();
				gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + " wins!");
				// TODO: highlight or make buttons flash?
			} else if (buttonsFilled >= 42) {
				endGame();
			} else {
				currentPlayer *= -1; // switch player
				gameStatus.setText((currentPlayer == 1 ? names[0] : names[1]) + "'s turn"); // update header
				gameStatus.setBackground(currentPlayer == 1 ? Color.RED : Color.YELLOW); // give visual indication of turn
			}
		}
	}

	public void endGame() {
		for (int i = 0; i < buttonGrid.length; i++) {
			for (JButton butt : buttonGrid[i]) {
				if (!legacyGraphics) butt.setContentAreaFilled(false);
				butt.setRolloverEnabled(false);
				butt.removeActionListener(this);
			}
		}
		headerButtons[0].setText("Quit");
		gameStatus.setText("It's a draw!");
		gameStatus.setBackground(null);
		gameOver = true;
		// TODO: if implementing move timer pause it
	}

	public boolean isGameOver() {
		return this.gameOver;
	}

	public void setDifficulty(int cpuDifficulty) {
		this.cpuDifficulty = cpuDifficulty;
	}

	public void setTimer(int moveTimerRemaining) {
		if (moveTimerRemaining > 0) {
			this.moveTimer.setText("" + moveTimerRemaining);
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
}