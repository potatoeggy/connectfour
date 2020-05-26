// Daniel Chen
// 13 May 2020
// main window for user interaction

import java.awt.event.*;
import javax.swing.*;

public class MainWindow extends JFrame implements ActionListener {
	private NewGameMenu newGameMenu;
	private MainMenu mainMenu;
	public MainWindow() {
		newGameMenu = new NewGameMenu(this);
		mainMenu = new MainMenu(this);

		add(mainMenu);
		add(newGameMenu);
		mainMenu.setVisible(true);
		newGameMenu.setVisible(false);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // make margins so it's nicer to use
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Connect 4");
		transition(newGameMenu, mainMenu);
	}

	private void transition(JPanel toRemove, JPanel toAdd) { // enables and disables panels as necessary
		// resize the frame
		if (toAdd.equals(mainMenu)) {
			setSize(300, 200);
		} else if (toAdd.equals(newGameMenu)) {
			setSize(800, 300);
		}
		
		// make panels change
		toRemove.setVisible(false);
		toAdd.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		Object e = event.getSource();

		// main menu interactions
		if (e.equals(mainMenu.buttons[0])) {
			transition(mainMenu, newGameMenu);
		} else if (e.equals(mainMenu.buttons[1])) {
			// load menu
		} else if (e.equals(mainMenu.buttons[2])) {
			dispose();
		}
		
		// new game menu interactions
		else if (e.equals(newGameMenu.buttons[0])) {
			System.out.println(0); // TODO: make them do something
		} else if (e.equals(newGameMenu.buttons[1])) {
			System.out.println("1");
		} else if (e.equals(newGameMenu.buttons[2])) {
			transition(newGameMenu, mainMenu);
		}
	}

	public static void main(String[] args) {
		MainWindow win = new MainWindow();
		win.setVisible(true);
	}
}
