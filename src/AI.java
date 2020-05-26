import java.util.PriorityQueue;

class coordinate implements Comparable<coordinate> {
    int x, y, color, rowSize, xM, yM;

    coordinate(int x, int y, int color, int rowSize, int xM, int yM) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.rowSize = rowSize;
        this.xM = xM;
        this.yM = yM;
    }

    @Override
    public int compareTo(coordinate o) {
        if (this.x == o.x) return this.y - o.y;
        return -1 * (this.x - o.x);
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
            int[][] rowSizes = new int[b.H][b.W];
            final int xM[] = {0, -1, 0, -1, -1}, yM[] = {1, 0, -1, 1, -1};
            for (int i = 0; i < b.W; i++) {
                PriorityQueue<coordinate> q = new PriorityQueue<>();
                if (board[b.H - 1][i] == 0 || visited[b.H - 1][i]) continue;
                q.add(new coordinate(b.H - 1, i, board[b.H - 1][i], 1, 0, 0));
                visited[b.H - 1][i] = true;
                while (!q.isEmpty()) {
                    coordinate curr = q.poll();
                    System.out.println(curr.x + "/" + curr.y + " " + curr.rowSize);
                    if (curr.rowSize > rowSize) {
                        ai = 0;
                        p = 0;
                        rowSize = curr.rowSize;
                    }
                    if (curr.rowSize == rowSize) {
                        if (curr.color == 1) ai++;
                        else p++;
                    }
                    for (int j = 0; j < 5; j++) {
                        //debug
                        /*System.out.println(curr.x + xM[j] >= 0);
                        System.out.println(curr.x + xM[j] < b.H);
                        System.out.println(curr.y + yM[j] >= 0);
                        System.out.println(curr.y + yM[j] < b.W);
                        System.out.println((curr.x + xM[j]) + " " + (curr.y + yM[j]));
                        System.out.println();*/
                        if (curr.x + xM[j] >= 0 && curr.x + xM[j] < b.H && curr.y + yM[j] >= 0 && curr.y + yM[j] < b.W && board[curr.x + xM[j]][curr.y + yM[j]] != 0) {
                            if (!visited[curr.x + xM[j]][curr.y + yM[j]]) {
                                if (curr.color != board[curr.x + xM[j]][curr.y + yM[j]]) {
                                    System.out.println("OUT" + board[curr.x + xM[j]][curr.y + yM[j]]);
                                    q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], board[curr.x + xM[j]][curr.y + yM[j]], 1, xM[j], yM[j]));
                                    rowSizes[curr.x + xM[j]][curr.y + yM[j]] = 1;
                                } else {
                                    if ((xM[j] == curr.xM && yM[j] == curr.yM) || curr.yM == 0 && curr.xM == 0) {
                                        System.out.println("IN" + curr.color);
                                        q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, curr.rowSize + 1, xM[j], yM[j]));
                                    } else {
                                        System.out.println("IN!!" + curr.color);
                                        q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, 1, xM[j], yM[j]));
                                    }
                                    rowSizes[curr.x + xM[j]][curr.y + yM[j]] = curr.rowSize + 1;
                                }
                                visited[curr.x + xM[j]][curr.y + yM[j]] = true;
                            } else if (curr.color == board[curr.x + xM[j]][curr.y + yM[j]] && curr.rowSize + 1 >= rowSizes[curr.x + xM[j]][curr.y + yM[j]] && xM[j] == curr.xM && yM[j] == curr.yM) {
                                q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, curr.rowSize + 1, xM[j], yM[j]));
                                rowSizes[curr.x + xM[j]][curr.y + yM[j]] = curr.rowSize + 1;
                            }
                        }
                    }
                }
            }
            System.out.println(rowSize + " " + ai + " " + p);
            return rowSize * (ai - p); //returns negative if player has more consecutive chips than AI
        }
    }

}
