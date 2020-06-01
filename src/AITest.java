import java.util.Arrays;

class AITest {

	public static void main(String[] args) {
		Board board = new Board();
		int[][] b = new int[board.H][board.W];
		b[5][2] = b[4][3] = b[5][4] = b[4][4] = b[4][6] = 2;
		b[5][3] = b[5][5] = b[5][6] = 1;
		board.board = b;
		printBoard(board);
		System.out.println(AI.minMax(board, 4, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
		/*int x = 5, y = 3, p = 2;
		board.board[5][3] = 1;
		while (!board.checkWin(x, y, p)) {
			if (p == 2) {
				board.board[new Scanner(System.in).nextInt()][new Scanner(System.in).nextInt()] = 2;
				p = 1;
				continue;
			}
			ArrayList<Integer> value = AI.minMax(board,
					4,
					1,
					Integer.MIN_VALUE,
					Integer.MAX_VALUE);
			int best = Integer.MIN_VALUE, bestIn = -1;
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
			y = bestIn;
			x = AI.nextEmpty(board, y);
			System.out.println(x + " " + y + " " + p);
			board.board[x][y] = p;
			System.out.println(Arrays.toString(value.toArray()));
			printBoard(board);
			System.out.println();
			p = 2;
		}*/
	}

	static void printBoard(Board b) {
		for (int i = 0; i < b.board.length; i++) {
			System.out.println(Arrays.toString(b.board[i]));
		}
		System.out.println();
	}
}