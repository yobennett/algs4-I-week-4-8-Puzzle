import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bennett on 4/1/15.
 */
public class Board {

    private final int n;
    private final int[][] blocks;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        if (blocks == null) {
            throw new NullPointerException();
        }

        this.n = blocks.length;
        this.blocks = blocks;
    }

    public int[][] blocks() {
        return blocks;
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

        // check last block
        if (blocks[n-1][n-1] != 0) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!isInPlace(i,j)) {
                    return false;
                }
            }
        }
        return true;
    }

    // a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        int[][] twinBlocks = new int[n][n];

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                int tmp = blocks[i][0];
                // TODO System.arraycopy from 2..n-1
                for (int j = 0; j < n; j++) {
                   if (j == 0) {
                       twinBlocks[i][j] = blocks[i][1];
                   } else if (j == 1) {
                       twinBlocks[i][j] = tmp;
                   } else {
                       twinBlocks[i][j] = blocks[i][j];
                   }
                }
            } else {
                System.arraycopy(blocks[i], 0, twinBlocks[i], 0, n);
            }
        }

        return new Board(twinBlocks);
    }

    // does this board equal that?
    @Override
    public boolean equals(Object y) {
        if (this == y)
            return true;
        if (y == null)
            return false;
        if (!(y instanceof Board))
            return false;
        Board that = (Board) y;
        return Arrays.deepEquals(this.blocks, that.blocks());
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31*hash + blocks.hashCode();
        return hash;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new ArrayList<Board>();
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%2d ", blocks[i][j]));
            }
            sb.append("\n");
        }

        sb.append("hamming: " + hamming() + "\n");
        sb.append("manhattan: " + manhattan() + "\n");
        sb.append("goal?: " + isGoal() + "\n");

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Starting....");

        int[][] input = { {8, 1, 3}, {4, 0, 2}, {7, 6, 5} };
        Board board = new Board(input);
        System.out.println(board);

        Board identicalTwin = new Board(input);
        System.out.println("Equals identicalTwin? " + board.equals(identicalTwin));

        int[][] input2 = { {5, 1, 3}, {4, 0, 2}, {7, 6, 8} };
        Board board2 = new Board(input2);
        System.out.println("Equals board2? " + board.equals(board2));

        System.out.println("\nTwin:\n" + board.twin());
    }

}
