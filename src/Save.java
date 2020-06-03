import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Save {
	public static void saveGame(Board board,
	                            int currentPlayer,
	                            int cpuDifficulty,
	                            int[] players,
	                            String[] names,
	                            int internalTurnCount,
	                            int buttonsFilled,
	                            boolean actionLock) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File("savedGame.txt"));

		//saves the board state
		for (int i = 0; i < board.H; i++) {
			for (int j = 0; j < board.W; j++) {
				printWriter.print(board.board[i][j] + " ");
			}
			printWriter.println();
		}

		printWriter.print(currentPlayer + " " + cpuDifficulty);

		for (int i : players) printWriter.print(i + " ");
		printWriter.println();
		for (String string : names) printWriter.print(string + " ");
		printWriter.println();

		printWriter.print(internalTurnCount + " " + buttonsFilled + " " + actionLock);
	}


}
