import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

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

	static Board b = new Board();

	static int scoreGen(int[][] board) {
		b.board = board;

		//checks all chips if there is a win
		for (int i = b.H - 1; i >= 0; i--) {
			for (int j = 0; j < b.W; j++) {
				if (board[i][j] == 0) continue; //if position empty - continue
				if (b.checkWin(i, j, 1)) { //if this is a winning move
					//System.out.println("AI WIN");
					return Integer.MAX_VALUE - 1; //if AI wins
				} else if (b.checkWin(i, j, 2)) {
					//System.out.println("PLAYER WIN");
					return Integer.MIN_VALUE + 1; //if player wins
				}
			}
		}

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
						//if there is a gap in the middle of a row
						else if (curr.color < 3) {
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

	static ArrayList<Integer> minMax(Board board, int depth, int player, int alpha, int beta) {
		ArrayList<Integer> ans = new ArrayList<>(); //best answer for current player
		for (int i = 0; i < board.W; i++) {

			Board b = new Board(); //copy of board
			ArrayList<Integer> value = new ArrayList<>(); //value of each move

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
					value.addAll(minMax(b, depth - 1, 1, alpha, beta));

					int max = Integer.MIN_VALUE;
					for (int j : value) {
						max = Math.max(max, j);
					}

					ans.add(max);
					//System.out.println(max + "!");
					//alpha = Math.max(alpha, max);

				} else { //Player - find smallest value
					value.addAll(minMax(b, depth - 1, 2, alpha, beta));

					int min = Integer.MAX_VALUE;
					for (int j : value) {
						min = Math.min(min, j);
					}

					ans.add(min);
					//System.out.println(min + "?");
					//beta = Math.min(beta, min);
				}

				//Alpha-Beta pruning
				if (beta <= alpha) {
					//System.out.println("Pruned" + depth + " " + alpha + " " + beta);
					break;
				}

			} else { //generate score for moves
				ans.add(scoreGen(b.board));
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
			return (int) (Math.random() * 7); // it's incredibly easy
		} else if (difficulty == 1) { // modify depth based on current difficulty
			depth = 2;
		} else if (difficulty == 2) {
			depth = 3;
		} else {
			depth = 4;
		}
		ArrayList<Integer> bestRows = minMax(board, depth, 1, Integer.MIN_VALUE, Integer.MAX_VALUE); // grab value from big algorithm

		bestIndex = 0;
		bestScore = bestRows.get(0);
		for (int i = 0, col = 0; i < bestRows.size() && i < board.W; i++, col++) { // iterate and find highest value
			if (nextEmpty(board, col) == -1) {
				i--;
				continue;
			}
			if (bestRows.get(i) > bestScore) {
				bestScore = bestRows.get(i);
				bestIndex = col;
			}
		}
		if (bestIndex == -1) System.out.println(Arrays.toString(bestRows.toArray()));
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
