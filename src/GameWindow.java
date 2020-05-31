// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameWindow extends JPanel implements ActionListener {
	private JPanel header, body; // two panels for main game and status
	private JPanel[] headerJustification; // align things left, centre, and right
	JButton[] headerButtons; // new game, save and quit
	JButton optionsButton; // options
	private JLabel gameStatus, moveTimer; // status and timer, if enabled
	private JButton[][] buttonGrid = new JButton[7][7]; // connect 4 grid
	private ImageIcon arrow, redPiece, yellowPiece; // graphics
	private MainWindow eventHandler; // global event handler and settings manager

	public GameWindow(MainWindow eventHandler, Board board) {
		this.eventHandler = eventHandler;
		header = new JPanel();
		headerJustification = new JPanel[] {new JPanel(), new JPanel(), new JPanel()};
		body = new JPanel();
		headerButtons = new JButton[] {new JButton("Save & quit"), new JButton("New game")};
		optionsButton = new JButton("Options");
		gameStatus = new JLabel("temporary header");
		moveTimer = new JLabel("move timer");
		try {
			arrow = new ImageIcon("resources/arrow.png");
			redPiece = new ImageIcon("resources/red.png");
			yellowPiece = new ImageIcon("resources/yellow.png");
		} catch (Exception e) {
			e.printStackTrace();
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
				buttonGrid[i][j].putClientProperty("x", new Integer(i)); // store coordinates
				buttonGrid[i][j].putClientProperty("y", new Integer(j));
				buttonGrid[i][j].setBackground(Color.WHITE); // make pretty
				buttonGrid[i][j].setFocusPainted(false); // do not have ugly lines
				buttonGrid[i][j].addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent event) { // highlight column when mouse goes over things
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						buttonGrid[0][y].setIcon(arrow);
						revalidate();
						repaint();
					}

					public void mouseExited(MouseEvent event) { // get rid of highlight when mouse leaves
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						buttonGrid[0][y].setIcon(null);
					}
				});
				body.add(buttonGrid[i][j]);
			}
		}

		for (int i = 0; i < buttonGrid[0].length; i++) {
			buttonGrid[0][i].setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE)); // make top open-ended
		}

		add(header);
		add(body);
		header.setLayout(new GridLayout(1, 3));
		body.setLayout(new GridLayout(7, 7, -1, -1));
		body.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setVisible(false);
	}

	public void actionPerformed(ActionEvent event) { // this is going to contain a lot of stuff too
		// TODO: reinitialise all buttons (probably init function) after leaving 
		int column = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
		int row = eventHandler.getBoard().addChip(column, eventHandler.getCurrentPlayer());
		if (row != -1) {
			buttonGrid[row+1][column].setBackground(eventHandler.getCurrentPlayer() == 1 ? Color.RED : Color.YELLOW); // unfortunately used to determine if something is occupying the square
			buttonGrid[row+1][column].setText(null); // centre icon
			buttonGrid[row+1][column].setIcon(eventHandler.getCurrentPlayer() == 1 ? redPiece : yellowPiece); // make it known to user (no idea how to force square gridlayouts)
			buttonGrid[row+1][column].setRolloverEnabled(false);
			buttonGrid[row+1][column].setContentAreaFilled(false); // "disable" button because setEnabled is garbage and makes everything gray
			buttonGrid[row+1][column].removeActionListener(this);
			if (eventHandler.getBoard().checkWin(row, column, eventHandler.getCurrentPlayer())) { // check if a player wins
				System.out.println("win");
			}
			// TODO: switch players here
		}
	}
}