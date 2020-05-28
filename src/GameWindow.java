// Daniel Chen
// 28 May 2020
// The bulk of the annoying stuff to do

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameWindow extends JPanel implements ActionListener {
	private JPanel header, body;
	private JPanel[] headerJustification;
	private JButton[] headerButtons;
	private JLabel gameStatus, moveTimer;
	public GameWindow(ActionListener eventHandler) {
		header = new JPanel();
		headerJustification = new JPanel[] {new JPanel(), new JPanel(), new JPanel()};
		body = new JPanel();
		headerButtons = new JButton[] {new JButton("Save and quit"), new JButton("New game"), new JButton("Options")};
		gameStatus = new JLabel("temporary header");
		moveTimer = new JLabel("temporary move timer");

		for (JButton butt : headerButtons) {
			headerJustification[0].add(butt);
			butt.addActionListener(eventHandler);
		}

		headerJustification[1].add(gameStatus);
		headerJustification[2].add(moveTimer);

		for (JPanel pan : headerJustification) {
			header.add(pan);
		}
		headerJustification[0].setLayout(new FlowLayout(FlowLayout.LEFT));
		headerJustification[1].setLayout(new FlowLayout(FlowLayout.CENTER));
		headerJustification[2].setLayout(new FlowLayout(FlowLayout.RIGHT));

		add(header);
		add(body);
		header.setLayout(new GridLayout(1, 3));
		body.setLayout(new GridLayout(7, 7, -1, -1));
		body.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setVisible(false);
	}

	public void actionPerformed(ActionEvent event) { // this is going to contain a lot of stuff too

	}
}