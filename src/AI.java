import java.util.*;

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

public class AI {

	final static boolean abPruning = false;
	static Board b = new Board();

	static int scoreGen(int[][] board) {
		b.board = board;

		//counts the largest amount of consecutive chips for each player
		int ai = 0, p = 0, rowSize = 0;
		final int xM[] = {0, -1, -1, -1}, yM[] = {1, 0, 1, -1}; //directions

		//array to keep track of which rows are already counted
		ArrayList<Integer[]> foundRows = new ArrayList<>();
		int xStart, yStart;

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

	static TreeMap<Integer, Integer> minMax(Board board, int depth, int player, int alpha, int beta) {
		TreeMap<Integer, Integer> ans = new TreeMap<>(); //best answer for current player
		for (int i = 0; i < board.W; i++) {

			Board b = new Board(); //copy of board
			TreeMap<Integer, Integer> value = new TreeMap<>(); //value of each move

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
						ans.put(i, Integer.MIN_VALUE);
						if (abPruning)
							beta = Integer.MIN_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 1, alpha, beta);

					int max = Integer.MIN_VALUE, index = -1;
					for (Map.Entry<Integer, Integer> j : value.entrySet()) {
						max = Math.max(max, j.getValue());
						if (max == j.getValue()) index = i;
					}

					ans.put(index, max);
					//System.out.println(max + "!");
					if (abPruning)
						beta = Math.min(beta, max);

				} else { //Player - find smallest value
					//check if this position already gives a win
					if (b.checkWin(x, i, 1)) {
						ans.put(i, Integer.MAX_VALUE);
						if (abPruning)
							alpha = Integer.MAX_VALUE;
						continue;
					}

					value = minMax(b, depth - 1, 2, alpha, beta);

					int min = Integer.MAX_VALUE, index = -1;
					for (Map.Entry<Integer, Integer> j : value.entrySet()) {
						min = Math.min(min, j.getValue());
						if (j.getValue() == min) index = i;
					}

					ans.put(i, min);
					//System.out.println(min + "?");
					if (abPruning)
						alpha = Math.max(alpha, min);
				}

				//Alpha-Beta pruning
				if (abPruning && beta <= alpha) {
					//System.out.println("Pruned" + depth + " " + alpha + " " + beta);
					break;
				}

			} else { //generate score for moves
				if (b.checkWin(x, i, player)) ans.put(i, player == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE);
				else ans.put(i, scoreGen(b.board));
				//System.out.println(ans.get(ans.size()-1)); //debug
			}

		}
		//System.out.println(Arrays.toString(ans.toArray()) + " " + depth);
		//System.out.println(alpha + " " + beta);
		//AITest.printBoard(b); //debug
		return ans;
	}

	// utility function maintained by potatoeggy to act as a stable interface between Ai and gui
	static int bestColumn(Board board, int difficulty) {
		int bestIndex, bestScore;
		int depth;
		if (difficulty == 0) {
			int temp = -1;
			while (nextEmpty(board, temp) == -1) { // make sure we don't choose invalid squares
				temp = (int) (Math.random() * 7);
			}
			return temp;
		} else if (difficulty == 1) { // modify depth based on current difficulty
			depth = 2;
		} else if (difficulty == 2) {
			depth = 3;
		} else {
			depth = 4;
		}
		TreeMap<Integer, Integer> bestRows = minMax(board, depth, 1, Integer.MIN_VALUE, Integer.MAX_VALUE); // grab value from big algorithm

		bestIndex = 0;
		bestScore = bestRows.get(0);
		for (Map.Entry<Integer, Integer> i : bestRows.entrySet()) { // iterate and find highest value
			System.out.println(i.getKey() + " " + i.getValue());
			if (i.getValue() >= bestScore) {
				bestScore = i.getValue();
				bestIndex = i.getKey();
			}
		}
		System.out.println();
		if (bestIndex == -1) System.out.println("Can't find an empty column");
		return bestIndex; // return to gui
	}

	//utility function to find next empty position in a column
	static int nextEmpty(Board board, int col) {
		for (int i = board.H - 1; i >= 0; i--) {
			if (board.board[i][col] == 0) return i;
		}
		return -1;
	}

	static boolean startBlocked(int x, int y, int xM, int yM) {
		return x - xM >= 0 && x - xM < b.H && y - yM >= 0 && y - yM < b.W && b.board[x - xM][y - yM] == 0;
	}

	static void reset() { // screw static variables
		b = new Board();
		//fullCol = new ArrayList<Integer>();
	}
}
