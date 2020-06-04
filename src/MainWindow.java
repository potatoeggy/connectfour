// Daniel Chen
// 13 May 2020
// main window for user interaction

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MainWindow extends JFrame implements ActionListener {
	// grab all the panels
	private final NewGameMenu newGameMenu;
	private final MainMenu mainMenu;
	private final OptionsMenu optionsMenu;
	private GameWindow gameWindow;
	private boolean optionsToNew; // where to go because the options menu should be accessible from in game and game creation menu

	// internal game variables
	private int[] players; // list of player types
	private String[] names; // list of player names
	private int cpuDifficulty; // 0-3 to pass to AI
	private int moveTimerInternal; // how much time is left (if negative, infinite) (might be handled by main come to think of it because we need threading)
	private int moveTimerFull; // what to reset timer to
	private int internalTurnCount; // not shown to user, used to enforce move timer

	/**
	 * Creates the master object to handle all other objects.
	 */
	public MainWindow() {
		// initialising internal variables
		cpuDifficulty = 1;
		moveTimerInternal = -1;
		moveTimerFull = -1;
		players = new int[2];
		names = new String[] {"Player 1", "Player 2"};
		internalTurnCount = 0;
		
		newGameMenu = new NewGameMenu(this);
		mainMenu = new MainMenu(this);
		optionsMenu = new OptionsMenu(this);
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
		if (toAdd.equals(mainMenu)) {
			if (new File("savedGame.txt").exists()) {
				mainMenu.enableLoad();
			} else {
				mainMenu.disableLoad();
			}
		}
		toAdd.setVisible(true);
		pack();
	}

	/**
	 * The global event handler for all objects.
	 * All ActionEvents that would affect other panels should be directed here.
	 */
	public void actionPerformed(ActionEvent event) { // global event handler (mostly used for panel transition)
		Object e = event.getSource();

		// main menu interactions
		if (e.equals(mainMenu.buttons[0])) {
			transition(mainMenu, newGameMenu);
		} else if (e.equals(mainMenu.buttons[1])) {
			if (retrieveSave()) {
				transition(mainMenu, gameWindow);
			}
		} else if (e.equals(mainMenu.buttons[2])) {
			dispose(); // exit and quit the program
		}
		
		// new game menu interactions
		else if (e.equals(newGameMenu.buttons[0])) {
			names = newGameMenu.getNames();
			players = newGameMenu.getPlayers();
			gameWindow = new GameWindow(this);
			AI.reset(); // it's not an object so we have to call something

			// pass information from new game and options, if configured
			gameWindow.setNames(names);
			gameWindow.setPlayers(players);
			gameWindow.setTimer(moveTimerInternal);
			if (players[0] == 1) gameWindow.cpuInit();
			add(gameWindow);
			transition(newGameMenu, gameWindow);
		} else if (e.equals(newGameMenu.buttons[1])) {
			optionsToNew = true;
			transition(newGameMenu, optionsMenu);
		} else if (e.equals(newGameMenu.buttons[2])) {
			transition(newGameMenu, mainMenu);
		}

		// options menu interactions
		else if (e.equals(optionsMenu.okButton)) {
			if (this.moveTimerFull != optionsMenu.getTimer()) { // only update timer if things change otherwise the timer resets when we enter options
				this.moveTimerFull = optionsMenu.getTimer();
				this.moveTimerInternal = moveTimerFull;
			}
			this.gameWindow.setTimer(moveTimerInternal);
			this.cpuDifficulty = optionsMenu.getDifficulty();
			transition(optionsMenu, optionsToNew ? newGameMenu : gameWindow);
		}

		// game window interactions
		else if (e.equals(gameWindow.optionsButton)) {
			optionsToNew = false;
			transition(gameWindow, optionsMenu);
		} else if (e.equals(gameWindow.headerButtons[0])) {
			if (!gameWindow.isGameOver()) {
				if (saveGame()) {
					dispose();
				}
				System.exit(0);
			} else {
				dispose();
			}
		} else if (e.equals(gameWindow.headerButtons[1])) {
			transition(gameWindow, newGameMenu);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		for (String s : new String[] {
			"Label",
			"Button",
			"ComboBox",
			"Panel"
		}) {
			UIManager.getLookAndFeelDefaults().put(s + ".background", Color.WHITE); // change default to make pretty
		}
		MainWindow win = new MainWindow();
		win.setVisible(true);

		while (win.isVisible()) { // exit when window is closed
			int currentPlayer = win.gameWindow.getCurrentPlayer();
			int responseTimer = 0;
			Thread.sleep(1); // really stupid workaround because for whatever reason we don't enter the next loop sometimes
			while (win.gameWindow.isVisible() && !win.gameWindow.isGameOver()) { // do not run timer when game is not ongoing
				if (win.gameWindow.getLock()) { // if event thread is waiting for ai calculation
					int col = AI.bestColumn(win.gameWindow.getBoard(), win.cpuDifficulty); // calculate in different thread so we don't hang the main event thread
					win.gameWindow.toggleLock(); // disable lock
					win.gameWindow.toggleAllButtons();
					win.gameWindow.sendClick(col); // send click
					responseTimer = 0;
				}
				
				if (currentPlayer != win.gameWindow.getCurrentPlayer() || win.internalTurnCount != win.gameWindow.getBoardHistory().length()) { // players have switched, reset timer
					currentPlayer = win.gameWindow.getCurrentPlayer();
					win.moveTimerInternal = win.moveTimerFull;
					responseTimer = 0;
				}
				if (responseTimer == 5) { // 0.2 s per tick seems nice
					if (win.moveTimerFull > 0) { // only update timer if it is used
						win.moveTimerInternal--;
						if (win.moveTimerInternal == 0) { // when time runs out
							win.gameWindow.endGame(-1 * win.gameWindow.getCurrentPlayer()); // opposite player wins
						}
						win.gameWindow.setTimer(win.moveTimerInternal); // update label in window
					}
					responseTimer = 0;
				}
				win.internalTurnCount = win.gameWindow.getBoardHistory().length();
				Thread.sleep(200);
				responseTimer++;
			}
			if (win.gameWindow.isGameOver()) {
				win.moveTimerInternal = win.moveTimerFull;
			}
		}
	}

	public boolean saveGame() {
		try {
			PrintWriter printWriter = new PrintWriter("savedGame.txt");
			for (int i : players) printWriter.print(i + " ");
			printWriter.println();
			for (String string : names) printWriter.println(string);
			//saves the board state
			printWriter.println(gameWindow.getBoardHistory());
			for (int i : new int[] {gameWindow.getCurrentPlayer(),
									cpuDifficulty, moveTimerInternal, moveTimerFull}) {
				printWriter.println(i);
			}
			printWriter.close();
		} catch (Exception e) {
			System.err.println("Save failed.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean retrieveSave() {
		try {
			Scanner input = new Scanner(new File("savedGame.txt"));
			players = new int[]{input.nextInt(), input.nextInt()};
			input.nextLine();
			names = new String[] {input.nextLine(), input.nextLine()};
			gameWindow.loadGame(input.nextLine(), players, names, input.nextInt());
			cpuDifficulty = input.nextInt();
			moveTimerInternal = input.nextInt();
			moveTimerFull = input.nextInt();
			input.close();

			optionsMenu.setDifficulty(cpuDifficulty);
			optionsMenu.setTimer(moveTimerFull != -1, moveTimerFull);
		} catch (Exception ex) {
			System.err.println("Load failed.");
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
