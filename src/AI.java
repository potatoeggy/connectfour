import java.util.Arrays;
import java.util.PriorityQueue;

class Coordinate implements Comparable<Coordinate> {
    int x, y, color, rowSize,
            dirX, dirY; //used to keep track which direction the row is

    Coordinate(int x, int y, int color, int rowSize, int dirX, int dirY) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.rowSize = rowSize;
        this.dirX = dirX;
        this.dirY = dirY;
    }

    @Override
    public int compareTo(Coordinate square) {
        if (this.x == square.x) return this.y - square.y;
        return -1 * (this.x - square.x);
    }
}

public class AI {
    static int scoreGen(int[][] board, int x, int y, int player) {
        Board newBoard = new Board();
        newBoard.board = board;

        if (newBoard.checkWin(x, y, player)) { //if this is a winning move
            if (player == 1) return Integer.MAX_VALUE; //if AI wins
            return Integer.MIN_VALUE; //if player wins
        } else { //counts the largest amount of consecutive chips for each player
            int cpuPoints = 0, playerPoints = 0, rowSize = 0;
            final int dirX[] = {0, -1, -1, -1}, dirY[] = {1, 0, 1, -1}; //directions

            for (int i = 0; i < newBoard.W; i++) { //loops through all columns in case there is a gap
                PriorityQueue<Coordinate> queue = new PriorityQueue<>();
                if (board[newBoard.H - 1][i] == 0) continue; //if this place is empty

                //starting position direction is 0, 0 to differentiate
                queue.add(new Coordinate(newBoard.H - 1, i, board[newBoard.H - 1][i], 1, 0, 0));

                while (!queue.isEmpty()) {
                    Coordinate curr = queue.poll();

                    if (curr.rowSize > rowSize) { //checks if the chip's rowSize is larger than the largest row size
                        cpuPoints = 0;
                        playerPoints = 0; //set their count to 0
                        rowSize = curr.rowSize;
                    }
                    if (curr.rowSize == rowSize) { //or if it is equal to the largest row size
                        if (curr.color == 1) cpuPoints++;
                        else playerPoints++;
                    }

                    for (int j = 0; j < 4; j++) { //loops through all 4 directions
                        if (curr.x + dirX[j] >= 0 && curr.x + dirX[j] < newBoard.H && curr.y + dirY[j] >= 0 && curr.y + dirY[j] < newBoard.W
                                && board[curr.x + dirX[j]][curr.y + dirY[j]] != 0) {

                            if (curr.color != board[curr.x + dirX[j]][curr.y + dirY[j]]) { //if it's a different color
                                queue.add(new Coordinate(curr.x + dirX[j], curr.y + dirY[j], board[curr.x + dirX[j]][curr.y + dirY[j]], 1, dirX[j], dirY[j]));
                            } else { //if it is the same color
                                //if the row direction is the same or if it came from the starting position
                                if ((dirX[j] == curr.dirX && dirY[j] == curr.dirY) || curr.dirY == 0 && curr.dirX == 0) {
                                    queue.add(new Coordinate(curr.x + dirX[j], curr.y + dirY[j], curr.color, curr.rowSize + 1, dirX[j], dirY[j]));
                                } else { //direction not the same
                                    queue.add(new Coordinate(curr.x + dirX[j], curr.y + dirY[j], curr.color, 1, dirX[j], dirY[j]));
                                }
                            }
                        }
                    }
                }
            }
            //returns negative if player has more consecutive chips than AI
            return rowSize * (cpuPoints - playerPoints);
        }
    }

    static int[] minMax(Board board, int depth, int player) {
        int ans[] = new int[7]; //best answer for current player

        for (int i = 0; i < board.W; i++) {
            System.out.println(player);
            Board b = new Board(); //copy of board
            int value[]; //value of each move
            b.board = new int[b.H][b.W];
            for (int j = 0; j < b.H; j++) b.board[j] = Arrays.copyOf(board.board[j], b.W);

            int x = nextEmpty(b, i); //next empty spot in column i
            if (x == -1) continue; //if out of empty spaces continue
            b.board[x][i] = player; //try the position

            test.printBoard(b);

            //recur until depth is 0
            if (depth != 0) {
                value = minMax(b, depth - 1, ((player - 1) ^ 1) + 1);
                if (player == 1) { //AI player - find largest value
                    int max = Integer.MIN_VALUE;
                    for (int j : value) {
                        max = Math.max(max, j);
                    }
                    ans[i] = max;
                } else { //Player - find smallest value
                    int min = Integer.MAX_VALUE;
                    for (int j : value) {
                        min = Math.min(min, j);
                    }
                    ans[i] = min;
                }
            } else {
                System.out.println("end");
                ans[i] = scoreGen(b.board, x, i, player);
            } //generate score for moves
        }
        return ans;
    }

    //utility function to find next empty position in a column
    static int nextEmpty(Board board, int col) {
        for (int i = board.H - 1; i >= 0; i--) {
            if (board.board[i][col] == 0) return i;
        }
        return -1;
    }

}