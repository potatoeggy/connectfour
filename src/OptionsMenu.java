// Daniel Chen
// 26 May 2020
// gui configuration menu

import javax.swing.*;

public class OptionsMenu extends JDialog {
	public OptionsMenu() {
		super();
		setTitle("Options");
	}
	public static void main(String[] args) {
		OptionsMenu win = new OptionsMenu();
		win.setVisible(true);
	}
}