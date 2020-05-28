// Daniel Chen
// 26 May 2020
// First window to be seen by user

import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JPanel {
	private JLabel title; // label at top as a header
	JButton[] buttons; // interactable buttons

	public MainMenu(ActionListener eventHandler) {
		title = new JLabel("Connect 4");
		buttons = new JButton[] {new JButton("New game"), new JButton("Load game"), new JButton("Exit")};

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(title);
		title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		for (JButton butt : buttons) {
			butt.addActionListener(eventHandler); // pass button clicks to the main window manager
			add(Box.createVerticalStrut(10)); // spacing for prettiness
			add(butt);
			butt.setAlignmentX(JButton.CENTER_ALIGNMENT);
		}
		setVisible(false); // invisible by default
	} // end constructor
}