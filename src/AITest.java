import java.util.ArrayList;
import java.util.Arrays;

class AITest {

	public static void main(String[] args) {
		Board board = new Board();
		int x = 5, y = 3, p = 1;
		board.board[5][3] = 1;
		while (!board.checkWin(x, y, p)) {
			ArrayList<Integer> value = AI.minMax(board,
					4,
					p = ((p - 1) ^ 1) + 1,
					Integer.MIN_VALUE,
					Integer.MAX_VALUE);
			int best = p == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE, bestIn = -1;
			for (int i = 0; i < value.size(); i++) {
				if (AI.nextEmpty(board, i) == -1) continue;
				if (p == 1 && best < value.get(i)) {
					best = value.get(i);
					bestIn = i;
				} else if (p == 2 && best > value.get(i)) {
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
		}
	}

	static void printBoard(Board b) {
		for (int i = 0; i < b.board.length; i++) {
			System.out.println(Arrays.toString(b.board[i]));
		}
	}
}