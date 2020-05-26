import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class test {
    public static void main(String[] args) throws IOException {
        int board[][] = new int[6][7];
        //board[5][4] = board[4][4] = board[3][4] = 1; //4
        board[5][4 - 1] = board[4][4 - 1] = board[3][4 - 1] = 2; //3
        board[5][4 - 2] = board[4][4 - 2] = board[3][4 - 2] = 2; //2
        System.out.println(board[4][4]);
        System.out.println(AI.scoreGen(board, 2, 4, 1));
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
}
