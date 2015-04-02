import java.util.ArrayList;

/**
 * Created by bennett on 4/1/15.
 */
public class Board {

    private final int n;
    private final int[][] blocks;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        this.n = blocks.length;
        this.blocks = blocks;
    }

    // board dimension N
    public int dimension() {
        return n;
    }

    // number of blocks out of place
    public int hamming() {
        int result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!isBlank(i,j) && !isInPlace(i,j)) {
                    result += 1;
                }
            }
        }
        return result;
    }

    private boolean isBlank(int i, int j) {
        return blocks[i][j] == 0;
    }

    private boolean isInPlace(int i, int j) {
        return blocks[i][j] == (i*n) + j + 1;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!isBlank(i,j) && !isInPlace(i,j)) {
                    result += distanceFromPlace(i,j);
                }
            }
        }
        return result;
    }

    private int distanceFromPlace(int i, int j) {
        int value = blocks[i][j];
        int targetRow = value / (n+1);
        int targetColumn = (value-1) % n;
        return Math.abs(targetRow - i) + Math.abs(targetColumn - j);
    }

    // is this board the goal board?
    public boolean isGoal() {
        return false;
    }

    // a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        return this;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        return false;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new ArrayList<Board>();
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int value;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                value = blocks[i][j];
                if (value == 0) {
                    sb.append(" ");
                } else {
                    sb.append(value);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Starting....");

        int[][] input = { {8, 1, 3}, {4, 0, 2}, {7, 6, 5} };
        Board board = new Board(input);
        System.out.println(board);
        System.out.println("hamming: " + board.hamming());
        System.out.println("manhattan: " + board.manhattan());
    }

}
