import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * A class to store all needed values for AI.scoreGen
 *
 * @see AI
 */
class coordinate implements Comparable<coordinate> {
	int x, y, color, rowSize, xBegin, yBegin,
			xM, yM; //used to keep track which direction the row is
	boolean startBlocked; //if the starting end is blocked - false

	coordinate(int x, int y, int color, int rowSize, int xM, int yM, int xBegin, int yBegin, boolean startBlocked) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.rowSize = rowSize;
		this.xM = xM;
		this.yM = yM;
		this.xBegin = xBegin;
		this.yBegin = yBegin;
		this.startBlocked = startBlocked;
	}

	@Override //sorted so it iterates through bottom left to top right - row by row
	public int compareTo(coordinate o) {
		if (this.x == o.x) return this.y - o.y;
		return -1 * (this.x - o.x);
	}
}

/**
 * @author Vincent
 * An AI that uses the Minimax algorithm to find the best move
 */
public class AI {

	final static boolean abPruning = false;
	static Board b = new Board();

	/**
	 * @param board current board state
	 * @return returns the value of this board state
	 * - large values mean AI advantage, smaller values mean player advantage
	 * @see coordinate
	 */
	static int scoreGen(int[][] board) {
		b.board = board;

		//counts the largest amount of consecutive chips for each player
		int ai = 0, p = 0, rowSize = 0;
		final int xM[] = {0, -1, -1, -1}, yM[] = {1, 0, 1, -1}; //directions

		//array to keep track of which rows are already counted
		ArrayList<Integer[]> foundRows = new ArrayList<>();
		int xStart, yStart; // TODO: remove these variables since vscode says they are unused

		//variable to keep count of score for chips closer to center
		int placementScore = 0;

		for (int i = 0; i < b.W; i++) { //loops through all columns in case there is a gap
			PriorityQueue<coordinate> q = new PriorityQueue<>();
			if (board[b.H - 1][i] == 0) continue; //if this place is empty

			//starting position
			q.add(new coordinate(b.H - 1, i, board[b.H - 1][i], 1, 0, 0, 5, i, false));

			while (!q.isEmpty()) {
				coordinate curr = q.poll();

				//find the actual color - if it's divisible by 10, then this is a gap
				int color = (curr.color % 10) == 0 ? curr.color / 10 : curr.color;

				//ending is unblocked?
				int trueRowSize = curr.startBlocked ? 1 : 0;
				if (curr.x + curr.xM >= 0 && curr.x + curr.xM < b.H && curr.y + curr.yM >= 0 && curr.y + curr.yM < b.W && board[curr.x + curr.xM][curr.y + curr.yM] == 0) {
					trueRowSize++;
				}
				trueRowSize = trueRowSize * curr.rowSize; //for each unblocked end in the row, multiply score

				//checks if the chip's rowSize is larger than the largest row size
				if (trueRowSize > rowSize && curr.color < 3) {
					ai = 0;
					p = 0; //set their count to 0
					rowSize = trueRowSize;
				}
				//or if it is equal to the largest row size
				if (trueRowSize == rowSize && curr.color < 3) {
					//checks if the row was already counted
					Integer[] beginEnd = {curr.xBegin, curr.yBegin, curr.x, curr.y};
					boolean found = false;
					for (Integer[] foundRow : foundRows) {
						if (Arrays.deepEquals(foundRow, beginEnd)) {
							found = true;
						}
					}

					if (!found) {
						foundRows.add(beginEnd);
						if (color == 1) ai++;
						else p++;
					}
				}

				for (int j = 0; j < 4; j++) { //loops through all 4 directions
					if (curr.x + xM[j] >= 0 && curr.x + xM[j] < b.H && curr.y + yM[j] >= 0 && curr.y + yM[j] < b.W) {
						if (board[curr.x + xM[j]][curr.y + yM[j]] != 0) { //if this position is not empty
							if (color != board[curr.x + xM[j]][curr.y + yM[j]]) { //if it's a different color
								q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], board[curr.x + xM[j]][curr.y + yM[j]], 1, xM[j], yM[j], curr.x + xM[j], curr.y + yM[j], false));
							} else { //if it is the same color
								//if the row direction is the same or if it came from the starting position
								if ((xM[j] == curr.xM && yM[j] == curr.yM) || curr.yM == 0 && curr.xM == 0) {
									boolean start = curr.startBlocked;
									if (curr.yM == 0 && curr.xM == 0 && j == 0) { //if horizontal direction is unblocked
										start = startBlocked(curr.x, curr.y, xM[j], yM[j]);
									}

									q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], color, curr.rowSize + 1, xM[j], yM[j], curr.xBegin, curr.yBegin, start));
								} else  //direction not the same
									q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], color, 1, xM[j], yM[j], curr.x + xM[j], curr.y + yM[j], startBlocked(curr.x + xM[j], curr.y + yM[j], xM[j], yM[j])));
							}
						}
						//if there is a gap in the middle of a row and it still has not yet reached rowSize of 3
						else if (curr.color < 3 && curr.rowSize != 3) {
							boolean start = curr.startBlocked;
							if (curr.yM == 0 && curr.xM == 0 && j == 0) { //if horizontal direction is unblocked
								start = startBlocked(curr.x, curr.y, xM[j], yM[j]);
							}
							q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], color * 10, curr.rowSize, xM[j], yM[j], curr.xBegin, curr.yBegin, start));
						}
					}
				}
			}

			for (int j = 5; j >= 0; j--) {
				if (board[j][i] == 0) break;
				if (board[j][i] == 1) placementScore += 4 - Math.abs(3 - i);
				else placementScore -= 4 - Math.abs(3 - i);
			}
		}
		//returns negative if player has more consecutive chips than AI
		return (rowSize * (ai - p)) * 2 + placementScore;
	}

	/**
	 * @param board  the current board state
	 * @param depth  how many moves forward the AI should see
	 * @param player minimizer or maximizer
	 * @param alpha  deprecated function that speeds up the AI
	 * @param beta   deprecated function that speeds up the AI
	 * @return returns a sorted tree with the generated score as the key and column as the value
	 */
	static TreeMap<Integer, Integer> minMax(Board board, int depth, int player, int alpha, int beta) {
		TreeMap<Integer, Integer> ans = new TreeMap<>(); //best answer for current player
		for (int i = 0; i < board.W; i++) {

			Board b = new Board(); //copy of board
			TreeMap<Integer, Integer> value; //value of each move

			b.board = new int[b.H][b.W];
			for (int j = 0; j < b.H; j++) b.board[j] = Arrays.copyOf(board.board[j], b.W);

			int x = nextEmpty(b, i); //next empty spot in column i
			if (x == -1) {//if column full continue
				continue;
			}

			b.board[x][i] = player; //try the position

			//recur until depth is 0
			if (depth != 0) {
				if (player == 2) { //AI - find largest value
					//check if this position already gives a win
					if (b.checkWin(x, i, 2)) {
						ans.put(Integer.MIN_VALUE, i);
						if (abPruning)
							beta = Integer.MIN_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 1, alpha, beta);


					ans.put(value.descendingMap().firstEntry().getKey(), i);
					//System.out.println(max + "!");
					if (abPruning)
						beta = Math.min(beta, value.descendingMap().firstEntry().getKey());

				} else { //Player - find smallest value
					//check if this position already gives a win
					if (b.checkWin(x, i, 1)) {
						ans.put(Integer.MAX_VALUE, i);
						if (abPruning)
							alpha = Integer.MAX_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 2, alpha, beta);


					ans.put(value.firstEntry().getKey(), i);
					//System.out.println(min + "?");
					if (abPruning)
						alpha = Math.max(alpha, value.firstEntry().getKey());
				}

				//Alpha-Beta pruning
				if (abPruning && beta <= alpha) { // TODO: remove debug statements
					break;
				}

			} else { //generate score for moves
				if (b.checkWin(x, i, player)) ans.put(player == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE, i);
				else ans.put(scoreGen(b.board), i);
				//System.out.println(ans.get(ans.size()-1)); //debug
			}

		}
		return ans;
	}

	/**
	 * utility function to act as a stable interface between Ai and gui
	 *
	 * @param board      current board state
	 * @param difficulty AI difficulty chosen by player
	 * @return returns an Integer corresponding to the AI's chosen column
	 * @see MainWindow
	 */
	static int bestColumn(Board board, int difficulty) {
		int depth;
		if (difficulty == 0) {
			int temp = -1;
			while (nextEmpty(board, temp) == -1) { // make sure we don't choose invalid squares
				temp = (int) (Math.random() * 7);
			}
			return temp;
		} else if (difficulty == 1) { // modify depth based on current difficulty
			depth = 1;
		} else if (difficulty == 2) {
			depth = 2;
		} else {
			depth = 3;
		}
		TreeMap<Integer, Integer> bestRows = minMax(board, depth, 1, Integer.MIN_VALUE, Integer.MAX_VALUE); // grab value from big algorithm

		System.out.println(bestRows.descendingMap());

		return bestRows.descendingMap().firstEntry().getValue(); // return to gui
	}

	/**
	 * utility function to find next empty position in a column
	 *
	 * @param board current board state
	 * @param col   chosen column
	 * @return returns an integer corresponding to the next empty row in the given column
	 */
	static int nextEmpty(Board board, int col) {
		for (int i = board.H - 1; i >= 0; i--) {
			if (board.board[i][col] == 0) return i;
		}
		return -1;
	}

	/**
	 * @param x  chosen position's row
	 * @param y  chosen position's column
	 * @param xM direction of current row
	 * @param yM direction of current row
	 * @return true if unblocked and false if blocked
	 */
	static boolean startBlocked(int x, int y, int xM, int yM) {
		return x - xM >= 0 && x - xM < b.H && y - yM >= 0 && y - yM < b.W && b.board[x - xM][y - yM] == 0;
	}

	/**
	 * utility function to reset the board. Used by the GUI.
	 *
	 * @see MainWindow
	 */
	static void reset() {
		b = new Board();
	}

}
