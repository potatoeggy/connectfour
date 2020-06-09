// Daniel Chen
// 26 May 2020
// New game options selection

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NewGameMenu extends JPanel {
	private JPanel playerBox; // panels used to hold things
	private JLabel title; // header
	private JLabel[] nameLabels, playerLabels; // instructions for user interaction
	private JComboBox<String>[] playerSelect; // select player
	private JTextField[] nameSelect; // select name
	JButton[] buttons; // continue or quit

	/**
	 * Create a new panel with options for the user to create a new game of Connect 4.
	 * @param eventHandler	An event handler that events that would cause the user to leave the panel will be passed to. 
	 */
	public NewGameMenu(ActionListener eventHandler) {
		// create all objects for frame menu
		playerBox = new JPanel();
		title = new JLabel("Create New Game");
		nameLabels = new JLabel[] {new JLabel("Name: "), new JLabel("Name: ")};
		playerLabels = new JLabel[] {new JLabel("Player type: "), new JLabel("Player type: ")};
		playerSelect = new JComboBox[] {new JComboBox<String>(), new JComboBox<String>()};
		nameSelect = new JTextField[] {new JTextField("Player 1"), new JTextField("Player 2")};
		buttons = new JButton[] {new JButton("Start game"), new JButton("Options"), new JButton("Return to main menu")};

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(title);
		add(Box.createVerticalStrut(10)); // add spacing
		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(playerBox);
		for (JButton butt : buttons) { // add all buttons and make them do something
			butt.setAlignmentX(JButton.CENTER_ALIGNMENT);
			butt.addActionListener(eventHandler);
			add(Box.createVerticalStrut(10)); // provide spacing to make it look nicer
			add(butt);
		}

		playerBox.setLayout(new GridLayout(2, 4, 5, 5));
		for (int i = 0; i < nameSelect.length; i++) { // add components to grid
			for (Component c : new Component[] {nameLabels[i], nameSelect[i], playerLabels[i], playerSelect[i]}) {
				playerBox.add(c);
			}
		}
		playerSelect[1].setEnabled(false); // only allow computers to go first

		// provide options for user to play against
		for (String s : new String[] {"Human", "Computer"}) {
			for (JComboBox<String> cb : playerSelect) { // provide options for both boxes
				cb.addItem(s);
			}
		}
		setVisible(false); // set invisible by default as visibility should be handed by main
	}

	/**
	 * Get the current names of the players.
	 * Players' names are entered in a JTextField on this panel.
	 * @return A String array containing two elements, one for each player.
	 */
	public String[] getNames() {
		return new String[] {nameSelect[0].getText(), nameSelect[1].getText()};
	}

	/**
	 * Get the current category of players.
	 * Players can be human- or computer-controlled. This is set by the user in a drop-down box.
	 * @return An int array containing two elements, one for each player.
	 */
	public int[] getPlayers() {
		return new int[] {playerSelect[0].getSelectedIndex(), playerSelect[1].getSelectedIndex()};
	}
}