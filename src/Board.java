public class Board {
    final int H = 6, W = 7; //board dimensions
    int[][] board = new int[H][W];

    boolean checkWin(int x, int y, int player) {
        int cL = 0, cU = 0, cRU = 0, cRD = 0;
        boolean lF = false, rF = false, uF = false, dF = false, ruF = false, ldF = false, luF = false, rdF = false;
        for (int i = 0; i <= H; i++) {
            if (!lF && y - i >= 0) {
                if (board[x][y - i] == player) cL++;
                else lF = true;
            }
            if (!rF && y + i < W) {
                if (board[x][y + i] == player) cL++;
                else rF = true;
            }
            if (!uF && x - i >= 0) {
                if (board[x - i][y] == player) cU++;
                else uF = true;
            }
            if (!dF && x + i < H) {
                if (board[x + i][y] == player) cU++;
                else dF = true;
            }
            if (!ruF && x - i >= 0 && y + i < W) {
                if (board[x - i][y + i] == player) cRU++;
                else ruF = true;
            }
            if (!ldF && x + i < H && y - i >= 0) {
                if (board[x + i][y - i] == player) cRU++;
                else ldF = true;
            }
            if (!rdF && x + i < H && y + i < W) {
                if (board[x + i][y + i] == player) cRD++;
                else rdF = true;
            }
            if (!luF && x - i >= 0 && y - i >= 0) {
                if (board[x - i][y - i] == player) cRD++;
                else luF = true;
            }
        }
        return cL >= 4 || cU >= 4 || cRD >= 4 || cRU >= 4;
    }
}
