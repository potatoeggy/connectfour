import java.util.Arrays;

class AITest {

	public static void main(String[] args) {
		Board board = new Board();
/*		int[][] b = new int[board.H][board.W];
		b[5][6] = b[4][6] = b[3][6] = 2;
		board.board = b;
		System.out.println(AI.minMax(board, 3, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));*/
		/*b[5][2] = b[4][3] = b[5][4] = b[4][4] = b[4][6] = 2;
		b[5][3] = b[5][5] = b[5][6] = 1;
		board.board = b;
		printBoard(board);
		System.out.println(AI.minMax(board, 4, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));*/
		/*int x = 5, y = 3, p = 1;
		while (!board.checkWin(x, y, p)) {
			int  bestIn = -1;
			else {
				ArrayList<Integer> value = AI.minMax(board,
						4,
						1,
						Integer.MIN_VALUE,
						Integer.MAX_VALUE);
				int best = Integer.MIN_VALUE;
				for (int i = 0, col = 0; i < value.size() && col < board.W; i++, col++) {
					if (AI.nextEmpty(board, col) == -1) {
						System.out.println(col + "/" + i);
						i--;
						continue;
					}
					if (best < value.get(i)) {
						best = value.get(i);
						bestIn = i;
					}
				}
			}
			y = bestIn;
			x = AI.nextEmpty(board, y);
			System.out.println(x + " " + y + " " + p);
			board.board[x][y] = p;
			printBoard(board);
			p = ((p-1) ^ 1) + 1;
		}*/
	}

	static void printBoard(Board b) {
		for (int i = 0; i < b.board.length; i++) {
			System.out.println(Arrays.toString(b.board[i]));
		}
		System.out.println();
	}
}