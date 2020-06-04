// Daniel Chen
// 26 May 2020
// gui configuration menu

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OptionsMenu extends JPanel implements ActionListener { // how do dialogs work I think I'll just use frames and timers instead
	JButton okButton;
	private JCheckBox moveTimerEnabler; // checkbox to enable a move timer, adds a dropdown box when enabled (consider just disabling the combobox instead)
	private JComboBox<String> difficultySelect, moveTimerSelect; // select computer difficulty and timer length via dropdowns
	private JLabel[] labels; // label what the boxes and settings do
	private JPanel[] fakeGrid; // gridlayout is the ugliest thing in the world so we fake it till we make it
	private JLabel title; // header at top

	/**
	 * Create a new panel containing various configuration options for Connect 4.
	 * @param eventHandler	The event handler that all events that would cause the user to leave the page are passed to.
	 */
	public OptionsMenu(ActionListener eventHandler) {
		fakeGrid = new JPanel[] {new JPanel(), new JPanel(), new JPanel(), new JPanel()}; // fake the grid
		title = new JLabel("Options");
		moveTimerEnabler = new JCheckBox();
		moveTimerSelect = new JComboBox<String>();
		difficultySelect = new JComboBox<String>();
		labels = new JLabel[] {new JLabel("Computer difficulty: "), new JLabel("Enable move timer: "), new JLabel()};
		okButton = new JButton("OK");

		for (String s : new String[] { // add options to difficulty select box
			"Easy",
			"Normal",
			"Hard",
			"Impossible"
		}) {
			difficultySelect.addItem(s);
		}
		difficultySelect.setSelectedItem("Normal");

		for (String s : new String[] { // add options to timer box
			"5 seconds",
			"10 seconds",
			"20 seconds",
			"30 seconds",
			"60 seconds"
		}) {
			moveTimerSelect.addItem(s);
		}
		moveTimerSelect.setSelectedItem("10 seconds");

		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		add(title);
		for (JPanel pan : fakeGrid) {
			pan.setLayout(new FlowLayout()); // set up fake gridlayout
			add(pan);
		}

		fakeGrid[0].add(labels[0]); // first row
		fakeGrid[0].add(difficultySelect);
		
		fakeGrid[1].add(labels[1]); // second row
		fakeGrid[1].add(moveTimerEnabler);
		moveTimerEnabler.addActionListener(this); // internal actionlistener because i don't think we need to go all the way back to mainwindow to handle something this trivial

		fakeGrid[2].add(labels[2]);
		fakeGrid[2].add(moveTimerSelect);
		moveTimerSelect.setEnabled(false); // invisible by default because the move timer is off by default

		fakeGrid[3].add(okButton);
		okButton.addActionListener(eventHandler); // pass it back because it requires panel switching

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setVisible(false); // visibility should be handled by MainWindow
	} // end constructor

	/**
	 * Handles events that originate and do not change anything outside of the panel.
	 * This only consists of enabling and disabling the drop-down for move timer options when the checkbox is enabled or disabled.
	 */
	public void actionPerformed(ActionEvent event) {
		Object e = event.getSource();
		if (e.equals(moveTimerEnabler)) {
			moveTimerSelect.setEnabled(moveTimerEnabler.isSelected()); // show extra move timer options if enabled only
		}
	} // end actionPerformed

	/**
	 * Returns the current difficulty set in the options menu.
	 * Ranges from 0 to 3 in ascending order of difficulty. Defaults to 1 if the user has not changed the difficulty.
	 * @return	An integer from 0 to 3, indicating level of difficulty in ascending order.
	 */
	public int getDifficulty() {
		return difficultySelect.getSelectedIndex();
	}

	public void setDifficulty(int index) {
		difficultySelect.setSelectedIndex(index);
	}

	/**
	 * Returns the current move timer length set in the options menu.
	 * The user-selected item in the combo box is converted to an integer if the move timer is enabled.
	 * @return	The number of seconds that the move timer should reset to, or -1 if the timer is disabled.
	 */
	public int getTimer() {
		return moveTimerEnabler.isSelected() ? Integer.parseInt(((String) moveTimerSelect.getSelectedItem()).substring(0, 2).trim()) + 1: -1; // returns it in seconds by parsing menu item
	}

	public void setTimer(boolean enabled, int seconds) {
		moveTimerEnabler.setSelected(enabled);
		moveTimerSelect.setEnabled(enabled);
		moveTimerSelect.setSelectedItem(seconds + " seconds");
	}
}
