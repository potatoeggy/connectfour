import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

public class test {
    public static void main(String[] args) throws IOException {
        int board[][] = new int[6][7];
        Board b = new Board();
        board[5][0] = board[5][2] = board[5][3] = 1;
        /*board[5][0] = 2;
        board[5][4] = board[4][4] = 1; //4
        board[3][4] = 2;
        board[5][4 - 1] = board[4][4 - 1] = board[3][4 - 1] = 2; //3
        board[5][4 - 2] = board[4][4 - 2] = board[3][4 - 2] = 2; //2
        board[5][5] = board[5][6] = 2;
        board[4][5] = board[4][6] = 1;*/
        b.board = board;
        System.out.println(AI.scoreGen(board));
        //System.out.println(Arrays.toString(AI.minMax(b, 3, 2)));
    }

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;

    static String next() throws IOException {
        while (st == null || !st.hasMoreTokens())
            st = new StringTokenizer(br.readLine().trim());
        return st.nextToken();
    }

    static long readLong() throws IOException {
        return Long.parseLong(next());
    }

    static int readInt() throws IOException {
        return Integer.parseInt(next());
    }

    static double readDouble() throws IOException {
        return Double.parseDouble(next());
    }

    static char readCharacter() throws IOException {
        return next().charAt(0);
    }

    static String readLine() throws IOException {
        return br.readLine().trim();
    }

    static void printBoard(Board board) {
        for (int i = 0; i < board.board.length; i++) {
            System.out.println(Arrays.toString(board.board[i]));
        }
    }
}
