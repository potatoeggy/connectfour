// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameWindow extends JPanel implements ActionListener {
	private JPanel header, body;
	private JPanel[] headerJustification;
	JButton[] headerButtons;
	JButton optionsButton;
	private JLabel gameStatus, moveTimer;
	private JButton[][] buttonGrid = new JButton[7][7];
	private ImageIcon arrow;
	private MainWindow eventHandler;

	public GameWindow(MainWindow eventHandler, Board board) {
		this.eventHandler = eventHandler;
		header = new JPanel();
		headerJustification = new JPanel[] {new JPanel(), new JPanel(), new JPanel()};
		body = new JPanel();
		headerButtons = new JButton[] {new JButton("Save and quit"), new JButton("New game")};
		optionsButton = new JButton("Options");
		gameStatus = new JLabel("temporary header");
		moveTimer = new JLabel("temporary move timer");
		arrow = new ImageIcon("resources/arrow.png");

		for (JButton butt : headerButtons) {
			headerJustification[0].add(butt);
			butt.addActionListener(eventHandler);
		}

		headerJustification[1].add(gameStatus);
		headerJustification[2].add(optionsButton);
		optionsButton.addActionListener(eventHandler);
		headerJustification[2].add(moveTimer);

		for (JPanel pan : headerJustification) {
			header.add(pan);
		}
		headerJustification[0].setLayout(new FlowLayout(FlowLayout.LEFT));
		headerJustification[1].setLayout(new FlowLayout(FlowLayout.CENTER));
		headerJustification[2].setLayout(new FlowLayout(FlowLayout.RIGHT));

		for (int i = 0; i < buttonGrid.length; i++) {
			for (int j = 0; j < buttonGrid[i].length; j++) {
				buttonGrid[i][j] = new JButton("<html><br><br><br><br><br><br></html>");
				buttonGrid[i][j].addActionListener(this);
				buttonGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				buttonGrid[i][j].putClientProperty("x", new Integer(i));
				buttonGrid[i][j].putClientProperty("y", new Integer(j));
				buttonGrid[i][j].setBackground(Color.WHITE);
				buttonGrid[i][j].setFocusPainted(false);
				buttonGrid[i][j].addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent event) {
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						buttonGrid[0][y].setIcon(arrow);
						revalidate();
						repaint();
					}

					public void mouseExited(MouseEvent event) {
						int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
						buttonGrid[0][y].setIcon(null);
					}
				});
				body.add(buttonGrid[i][j]);
			}
		}

		for (int i = 0; i < buttonGrid[0].length; i++) {
			buttonGrid[0][i].setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.WHITE));
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
		// this is temporary since the board class should be updated to handle column only things
		int x = (Integer) (((JButton) event.getSource()).getClientProperty("x"));
		int y = (Integer) (((JButton) event.getSource()).getClientProperty("y"));
		eventHandler.getBoard().addChip(x, y, eventHandler.getCurrentPlayer());
	}
}