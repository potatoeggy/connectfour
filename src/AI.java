import java.util.LinkedList;
import java.util.Queue;

class coordinate {
    int x, y, color, rowSize;

    coordinate(int x, int y, int color, int rowSize) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.rowSize = rowSize;
    }
}

public class AI {
    static int scoreGen(int[][] board, int x, int y, int player) {
        Board b = new Board();
        b.board = board;
        if (b.checkWin(x, y, player)) { //if this is a winning move
            if (player == 1) return Integer.MAX_VALUE; //if AI wins
            return Integer.MIN_VALUE; //if player wins
        } else { //counts the largest amount of consecutive chips for each player
            int ai = 0, p = 0, rowSize = 0;
            boolean[][] visited = new boolean[b.H][b.W];
            final int xM[] = {1, 0, -1, 0, 1, 1, -1, -1}, yM[] = {0, 1, 0, -1, 1, -1, 1, -1};
            for (int i = 0; i < b.W; i++) {
                Queue<coordinate> q = new LinkedList<>();
                if (board[b.H - 1][i] == 0 || visited[b.H - 1][i]) continue;
                q.add(new coordinate(b.H - 1, i, board[b.H - 1][i], 1));
                visited[b.H - 1][i] = true;
                while (!q.isEmpty()) {
                    coordinate curr = q.poll();
                    if (curr.rowSize > rowSize) {
                        ai = 0;
                        p = 0;
                        rowSize = curr.rowSize;
                    }
                    if (curr.rowSize == rowSize) {
                        if (curr.color == 1) ai++;
                        else p++;
                    }
                    for (int j = 0; j < 8; j++) {
                        //debug
                        /*System.out.println(curr.x + xM[j] >= 0);
                        System.out.println(curr.x + xM[j] < b.H);
                        System.out.println(curr.y + yM[j] >= 0);
                        System.out.println(curr.y + yM[j] < b.W);
                        System.out.println((curr.x + xM[j]) + " " + (curr.y + yM[j]));
                        System.out.println();*/
                        if (curr.x + xM[j] >= 0 && curr.x + xM[j] < b.H && curr.y + yM[j] >= 0 && curr.y + yM[j] < b.W && board[curr.x + xM[j]][curr.y + yM[j]] != 0 && !visited[curr.x + xM[j]][curr.y + yM[j]]) {
                            if (curr.color != board[curr.x + xM[j]][curr.y + yM[j]]) {
                                q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], board[curr.x + xM[j]][curr.y + yM[j]], 1));
                            } else {
                                System.out.println("IN");
                                q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, curr.rowSize + 1));
                            }
                            visited[curr.x + xM[j]][curr.y + yM[j]] = true;
                        }
                    }
                }
            }
            return rowSize * (ai - p); //returns negative if player has more consecutive chips than AI
        }
    }

}
