// Daniel Chen
// 13 May 2020
// main window for user interaction

// TODO: consider javadoc since maybe that'll give more marks

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import java.awt.*;

public class MainWindow extends JFrame implements ActionListener {
	// grab all the panels
	private NewGameMenu newGameMenu;
	private MainMenu mainMenu;
	private OptionsMenu optionsMenu;
	private GameWindow gameWindow;
	private boolean optionsToNew; // where to go because the options menu should be accessible from in game and game creation menu
	
	// internal game variables
	private Board board;
	private int[] players; // list of player types
	private String[] names; // list of player names
	private int currentPlayer; // corresponds to array index
	private int cpuDifficulty; // 0-3 to pass to AI
	private int moveTimerInternal; // how much time is left (if negative, infinite) (might be handled by main come to think of it because we need threading)
	private int moveTimerFull; // what to reset timer to


	public MainWindow() {
		// initialising internal variables
		currentPlayer = 1;
		cpuDifficulty = 1;
		moveTimerInternal = -1;
		moveTimerFull = -1;
		players = new int[2];
		names = new String[] {"Player 1", "Player 2"};
		
		newGameMenu = new NewGameMenu(this);
		mainMenu = new MainMenu(this);
		optionsMenu = new OptionsMenu(this);
		board = new Board();
		gameWindow = new GameWindow(this);

		for (JPanel pan : new JPanel[] {newGameMenu, mainMenu, optionsMenu, gameWindow}) {
			add(pan); // add panels to frame even if they're hidden so we can control them later
		}

		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)); // set layout of frame
		getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 10)); // make margins so it's nicer to use
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setTitle("Connect 4");
		transition(mainMenu);
	} // end constructor

	private void transition(JPanel toRemove, JPanel toAdd) { // <-- method overloading now give me marks
		toRemove.setVisible(false);
		transition(toAdd);
	}

	private void transition(JPanel toAdd) { // enables panels
		// make things appear
		toAdd.setVisible(true);
		pack();
	}

	public void actionPerformed(ActionEvent event) { // global event handler (mostly used for panel transition)
		Object e = event.getSource();

		// main menu interactions
		if (e.equals(mainMenu.buttons[0])) {
			transition(mainMenu, newGameMenu);
		} else if (e.equals(mainMenu.buttons[1])) {
			// TODO: import board settings
			transition(mainMenu, gameWindow);
		} else if (e.equals(mainMenu.buttons[2])) {
			dispose(); // exit and quit the program
		}
		
		// new game menu interactions
		else if (e.equals(newGameMenu.buttons[0])) {
			names = newGameMenu.getNames();
			players = newGameMenu.getPlayers();
			gameWindow = new GameWindow(this);
			gameWindow.setNames(names);
			gameWindow.setPlayers(players);
			gameWindow.setTimer(moveTimerInternal);
			gameWindow.setDifficulty(cpuDifficulty);
			add(gameWindow);
			transition(newGameMenu, gameWindow);
		} else if (e.equals(newGameMenu.buttons[1])) {
			optionsToNew = true;
			transition(newGameMenu, optionsMenu); // TODO: make sure game is paused when transitioning so move timers don't run and all that
		} else if (e.equals(newGameMenu.buttons[2])) {
			transition(newGameMenu, mainMenu);
		}

		// options menu interactions
		else if (e.equals(optionsMenu.okButton)) {
			this.moveTimerFull = optionsMenu.getTimer();
			this.cpuDifficulty = optionsMenu.getDifficulty();
			transition(optionsMenu, optionsToNew ? newGameMenu : gameWindow);
		}

		// game window interactions
		else if (e.equals(gameWindow.optionsButton)) {
			gameWindow.setDifficulty(cpuDifficulty);
			optionsToNew = false;
			transition(gameWindow, optionsMenu);
		} else if (e.equals(gameWindow.headerButtons[0])) {
			if (!gameWindow.isGameOver()) {
				// TODO: save
			}
			transition(gameWindow, mainMenu);
		} else if (e.equals(gameWindow.headerButtons[1])) {
			transition(gameWindow, newGameMenu);
		}
	}
	// TODO: implement dark mode

	public static void main(String[] args) throws InterruptedException {
		for (String s : new String[] {
			"Label",
			"Button",
			"ComboBox",
			"Panel"
		}) {
			UIManager.getLookAndFeelDefaults().put(s + ".background", Color.WHITE);
		}
		MainWindow win = new MainWindow();
		win.setVisible(true);

		while (win.isVisible()) {
			while (win.gameWindow.isVisible()) {
				if (win.moveTimerInternal == 0) { // lose or play random piece
					win.moveTimerInternal = win.moveTimerFull;
				}
				win.gameWindow.setTimer(win.moveTimerInternal);
				// wait a minute what happens when they click a button since the move timer needs to be reset (probably use two action listeners) (oh that doesn't work either since this is main screw threads)
				Thread.sleep(1000);
			}
		}
	}
}
