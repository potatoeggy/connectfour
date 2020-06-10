/**
 * An object to store the board state with a integrated win checking and chip adding function
 *
 * @author Vincent
 * @see AI for usage
 * @see GameWindow for usage
 */
public class Board {
	final int H = 6, W = 7; //board dimensions
	int[][] board = new int[H][W];

	/**
	 * A function to check if last placed chip creates a win
	 *
	 * @param x      position of the last placed piece
	 * @param y      position of the last placed piece
	 * @param player the player who placed the piece
	 * @return returns a boolean value: true for win; false for no win
	 * @see AI for usage
	 * @see GameWindow for usage
	 */
	public boolean checkWin(int x, int y, int player) {

		//if this chip does not equal to the current player
		if (board[x][y] != player) return false;

		//keeps count of the number of chips a row
		int cL = 1, cU = 1, cRU = 1, cRD = 1;
		//keeps track if the row is blocked
		boolean lF = false, rF = false,
				uF = false, dF = false,
				ruF = false, ldF = false,
				luF = false, rdF = false;

		//checks every chip around the last placed chip for a win
		for (int i = 1; i <= 5; i++) {
			if (!lF && y - i >= 0) {
				if (board[x][y - i] == player) cL++;
				else lF = true;
			}
			if (!rF && y + i < W) {
				if (board[x][y + i] == player) cL++;
				else rF = true;
			}
			if (!uF && x - i >= 0) {
				if (board[x - i][y] == player) cU++;
				else uF = true;
			}
			if (!dF && x + i < H) {
				if (board[x + i][y] == player) cU++;
				else dF = true;
			}
			if (!ruF && x - i >= 0 && y + i < W) {
				if (board[x - i][y + i] == player) cRU++;
				else ruF = true;
			}
			if (!ldF && x + i < H && y - i >= 0) {
				if (board[x + i][y - i] == player) cRU++;
				else ldF = true;
			}
			if (!rdF && x + i < H && y + i < W) {
				if (board[x + i][y + i] == player) cRD++;
				else rdF = true;
			}
			if (!luF && x - i >= 0 && y - i >= 0) {
				if (board[x - i][y - i] == player) cRD++;
				else luF = true;
			}
		}
		return cL >= 4 || cU >= 4 || cRD >= 4 || cRU >= 4;
	}

	/**
	 * returns the row and updates the board
	 *
	 * @param col    given column to add a chip
	 * @param player the current player
	 * @return an integer detonating the next empty row to the GUI
	 * @see AI for the addChip function
	 * @see GameWindow for usage
	 */
	public int addChip(int col, int player) {
		int row = AI.nextEmpty(this, col);
		if (row != -1) { // if all columns are filled make sure we don't crash
			board[row][col] = player;
		}
		return row;
	}
}
