import java.util.PriorityQueue;

class coordinate implements Comparable<coordinate> {
    int x, y, color, rowSize,
            xM, yM; //used to keep track which direction the row is

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
            final int xM[] = {0, -1, -1, -1}, yM[] = {1, 0, 1, -1}; //directions

            for (int i = 0; i < b.W; i++) { //loops through all columns in case there is a gap
                PriorityQueue<coordinate> q = new PriorityQueue<>();
                if (board[b.H - 1][i] == 0) continue; //if this place is empty

                //starting position direction is 0, 0 to differentiate
                q.add(new coordinate(b.H - 1, i, board[b.H - 1][i], 1, 0, 0));

                while (!q.isEmpty()) {
                    coordinate curr = q.poll();

                    if (curr.rowSize > rowSize) { //checks if the chip's rowSize is larger than the largest row size
                        ai = 0;
                        p = 0; //set their count to 0
                        rowSize = curr.rowSize;
                    }
                    if (curr.rowSize == rowSize) { //or if it is equal to the largest row size
                        if (curr.color == 1) ai++;
                        else p++;
                    }

                    for (int j = 0; j < 4; j++) { //loops through all 4 directions
                        if (curr.x + xM[j] >= 0 && curr.x + xM[j] < b.H && curr.y + yM[j] >= 0 && curr.y + yM[j] < b.W
                                && board[curr.x + xM[j]][curr.y + yM[j]] != 0) {

                            if (curr.color != board[curr.x + xM[j]][curr.y + yM[j]]) { //if it's a different color
                                q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], board[curr.x + xM[j]][curr.y + yM[j]], 1, xM[j], yM[j]));
                            } else { //if it is the same color
                                //if the row direction is the same or if it came from the starting position
                                if ((xM[j] == curr.xM && yM[j] == curr.yM) || curr.yM == 0 && curr.xM == 0) {
                                    q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, curr.rowSize + 1, xM[j], yM[j]));
                                } else { //direction not the same
                                    q.add(new coordinate(curr.x + xM[j], curr.y + yM[j], curr.color, 1, xM[j], yM[j]));
                                }
                            }
                        }
                    }
                }
            }
            return rowSize * (ai - p); //returns negative if player has more consecutive chips than AI
        }
    }

}