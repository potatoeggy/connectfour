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

	public void actionPerformed(ActionEvent event) {
		Object e = event.getSource();
		if (e.equals(moveTimerEnabler)) {
			moveTimerSelect.setEnabled(moveTimerEnabler.isSelected()); // i hope isSelected does what i think it does
		}
	} // end actionPerformed

	public int getDifficulty() {
		return moveTimerSelect.getSelectedIndex();
	}

	public int getTimer() {
		return Integer.parseInt(((String) moveTimerSelect.getSelectedItem()).substring(0, 2));
	}
}
