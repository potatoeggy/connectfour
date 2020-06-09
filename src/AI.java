import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * A class to store all needed values for AI.scoreGen
 *
 * @author Vincent
 * @see AI for usage
 */
class Coordinate implements Comparable<Coordinate> {
	int x, y, //position on board
			color, //the color of the current row
			rowSize, //the size of the current row
			xBegin, yBegin, //the start of the row
			xMove, yMove; //used to keep track which direction the row is

	boolean startBlocked; //if the starting end is blocked - false

	Coordinate(int x, int y,
	           int color,
	           int rowSize,
	           int xMove, int yMove,
	           int xBegin, int yBegin,
	           boolean startBlocked) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.rowSize = rowSize;
		this.xMove = xMove;
		this.yMove = yMove;
		this.xBegin = xBegin;
		this.yBegin = yBegin;

		this.startBlocked = startBlocked;
	}


	@Override
	//sorted so it iterates through bottom left to top right - row by row
	public int compareTo(Coordinate o) {
		if (this.x == o.x) return this.y - o.y;
		return -1 * (this.x - o.x);
	}
}

/**
 * An AI that uses the Minimax algorithm to find the best move
 *
 * @author Vincent
 */
public class AI {

	//alpha-beta pruning has been disable due to bugs in the AI
	final static boolean AB_PRUNING = false;
	static Board b = new Board();

	/**
	 * Generates a score for the current board state
	 * large values mean AI advantage, smaller values mean player advantage
	 *
	 * @param board current board state
	 * @return returns the value of this board state
	 * @see Coordinate an object used in this function
	 */
	static int scoreGen(int[][] board) {
		b.board = board;

		//counts the largest amount of consecutive chips for each player
		int ai = 0, p = 0, rowSize = 0;
		final int X_MOVE[] = {0, -1, -1, -1}, Y_MOVE[] = {1, 0, 1, -1}; //directions

		//array to keep track of which rows are already counted
		ArrayList<Integer[]> foundRows = new ArrayList<>();

		//variable to keep count of score for chips closer to center
		int placementScore = 0;

		for (int i = 0; i < b.W; i++) { //loops through all columns in case there is a gap
			PriorityQueue<Coordinate> q = new PriorityQueue<>();
			if (board[b.H - 1][i] == 0) continue; //if this place is empty

			//starting position
			q.add(new Coordinate(b.H - 1, i, board[b.H - 1][i], 1, 0, 0, 5, i, false));

			while (!q.isEmpty()) {
				Coordinate currentNode = q.poll();

				//find the actual color - if it's divisible by 10, then this is a gap
				int color = (currentNode.color % 10) == 0 ? currentNode.color / 10 : currentNode.color;

				//ending is unblocked?
				int trueRowSize = currentNode.startBlocked ? 1 : 0;
				if (currentNode.x + currentNode.xMove >= 0 && currentNode.x + currentNode.xMove < b.H && currentNode.y + currentNode.yMove >= 0 && currentNode.y + currentNode.yMove < b.W && board[currentNode.x + currentNode.xMove][currentNode.y + currentNode.yMove] == 0) {
					trueRowSize++;
				}
				trueRowSize = trueRowSize * currentNode.rowSize; //for each unblocked end in the row, multiply score

				//checks if the chip's rowSize is larger than the largest row size
				if (trueRowSize > rowSize && currentNode.color < 3) {
					ai = 0;
					p = 0; //set their count to 0
					rowSize = trueRowSize;
				}
				//or if it is equal to the largest row size
				if (trueRowSize == rowSize && currentNode.color < 3) {
					//checks if the row was already counted
					Integer[] beginEnd = {currentNode.xBegin, currentNode.yBegin, currentNode.x, currentNode.y};
					boolean found = false;
					for (Integer[] foundRow : foundRows) {
						if (Arrays.deepEquals(foundRow, beginEnd)) {
							found = true;
						}
					}

					//if row is not counted then record in found rows
					// and add to AI or player's score
					if (!found) {
						foundRows.add(beginEnd);
						if (color == 1) ai++;
						else p++;
					}
				}

				for (int j = 0; j < 4; j++) { //loops through all 4 directions
					if (currentNode.x + X_MOVE[j] >= 0 && currentNode.x + X_MOVE[j] < b.H && currentNode.y + Y_MOVE[j] >= 0 && currentNode.y + Y_MOVE[j] < b.W) {
						if (board[currentNode.x + X_MOVE[j]][currentNode.y + Y_MOVE[j]] != 0) { //if this position is not empty
							if (color != board[currentNode.x + X_MOVE[j]][currentNode.y + Y_MOVE[j]]) { //if it's a different color
								q.add(new Coordinate(currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], board[currentNode.x + X_MOVE[j]][currentNode.y + Y_MOVE[j]], 1, X_MOVE[j], Y_MOVE[j], currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], false));
							} else { //if it is the same color
								//if the row direction is the same or if it came from the starting position
								if ((X_MOVE[j] == currentNode.xMove && Y_MOVE[j] == currentNode.yMove) || currentNode.yMove == 0 && currentNode.xMove == 0) {
									boolean start = currentNode.startBlocked;
									if (currentNode.yMove == 0 && currentNode.xMove == 0 && j == 0) { //if horizontal direction is unblocked
										start = startBlocked(currentNode.x, currentNode.y, X_MOVE[j], Y_MOVE[j]);
									}

									q.add(new Coordinate(currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], color, currentNode.rowSize + 1, X_MOVE[j], Y_MOVE[j], currentNode.xBegin, currentNode.yBegin, start));
								} else  //direction not the same
									q.add(new Coordinate(currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], color, 1, X_MOVE[j], Y_MOVE[j], currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], startBlocked(currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], X_MOVE[j], Y_MOVE[j])));
							}
						}
						//if there is a gap in the middle of a row and it still has not yet reached rowSize of 3
						else if (currentNode.color < 3 && currentNode.rowSize != 3) {
							boolean start = currentNode.startBlocked;
							if (currentNode.yMove == 0 && currentNode.xMove == 0 && j == 0) { //if horizontal direction is unblocked
								start = startBlocked(currentNode.x, currentNode.y, X_MOVE[j], Y_MOVE[j]);
							}
							q.add(new Coordinate(currentNode.x + X_MOVE[j], currentNode.y + Y_MOVE[j], color * 10, currentNode.rowSize, X_MOVE[j], Y_MOVE[j], currentNode.xBegin, currentNode.yBegin, start));
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
	 * A standard Minimax algorithm adapted to connect 4
	 *
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
						if (AB_PRUNING)
							beta = Integer.MIN_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 1, alpha, beta);


					ans.put(value.descendingMap().firstEntry().getKey(), i);
					if (AB_PRUNING)
						beta = Math.min(beta, value.descendingMap().firstEntry().getKey());

				} else { //Player - find smallest value
					//check if this position already gives a win
					if (b.checkWin(x, i, 1)) {
						ans.put(Integer.MAX_VALUE, i);
						if (AB_PRUNING)
							alpha = Integer.MAX_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 2, alpha, beta);


					ans.put(value.firstEntry().getKey(), i);
					if (AB_PRUNING)
						alpha = Math.max(alpha, value.firstEntry().getKey());
				}

				//Alpha-Beta pruning
				if (AB_PRUNING && beta <= alpha) {
					break;
				}

			} else { //generate score for moves
				if (b.checkWin(x, i, player)) ans.put(player == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE, i);
				else ans.put(scoreGen(b.board), i);
			}

		}
		return ans;
	}

	/**
	 * utility function to act as a stable interface between AI and gui
	 *
	 * @param board      current board state
	 * @param difficulty AI difficulty chosen by player
	 * @return returns an Integer corresponding to the AI's chosen column
	 * @see MainWindow for usage
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

		// grab value from big algorithm and return to GUI
		return minMax(board,
				depth,
				1,
				Integer.MIN_VALUE,
				Integer.MAX_VALUE)
				.descendingMap()
				.firstEntry()
				.getValue();
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
	 * utility function to check if the start of a row is blocked
	 *
	 * @param x     chosen position's row
	 * @param y     chosen position's column
	 * @param xMove direction of current row
	 * @param yMove direction of current row
	 * @return true if unblocked and false if blocked
	 */
	static boolean startBlocked(int x, int y, int xMove, int yMove) {
		return x - xMove >= 0 && x - xMove < b.H && y - yMove >= 0 && y - yMove < b.W && b.board[x - xMove][y - yMove] == 0;
	}

	/**
	 * utility function to reset the board. Used by the GUI.
	 *
	 * @see MainWindow for usage
	 */
	static void reset() {
		b = new Board();
	}

}
