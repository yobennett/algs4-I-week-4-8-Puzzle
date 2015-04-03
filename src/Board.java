import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bennett on 4/1/15.
 */
public class Board {

    private final int n;
    private final int[][] blocks;

    // compare points by Manhattan priority
    public final Comparator<Board> MANHATTAN_PRIORITY = new ByManhattanPriority();

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
        return (blocks[n-1][n-1] == 0) && (manhattan() == 0);
    }

    // a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        return new Board(swap(0, 0, 0, 1));
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
        hash = 31*hash + Arrays.hashCode(blocks);
        return hash;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Set<Board> result = new HashSet<Board>(4);
        int[] blankCoordinates = coordinatesForBlank();
        int i = blankCoordinates[0];
        int j = blankCoordinates[1];

        // top
        if (withinBounds(i - 1, j)) {
            result.add(new Board(swap(i, j, i - 1, j)));
        }

        // right
        if (withinBounds(i, j + 1)) {
            result.add(new Board(swap(i, j, i, j + 1)));
        }

        // bottom
        if (withinBounds(i + 1, j)) {
            result.add(new Board(swap(i, j, i + 1, j)));
        }

        // left
        if (withinBounds(i, j - 1)) {
            result.add(new Board(swap(i, j, i, j - 1)));
        }

        return result;
    }

    private int[][] swap(int i1, int j1, int i2, int j2) {
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++) {
            System.arraycopy(blocks[i], 0, result[i], 0, n);
        }

        // swap
        int tmp = result[i1][j1];
        result[i1][j1] = result[i2][j2];
        result[i2][j2] = tmp;

        return result;
    }

    private int[] coordinatesForBlank() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n;  j++) {
                if (blocks[i][j] == 0) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[] {-1, -1};
    }

    private boolean withinBounds(int i, int j) {
        return (i >= 0) && (i < n) && (j >= 0) && (j < n);
    }

    private class ByManhattanPriority implements Comparator<Board> {

        @Override
        public int compare(Board b1, Board b2) {
            int manhattan1 = b1.manhattan();
            int manhattan2 = b2.manhattan();

            if (manhattan1 > manhattan2) {
                return 1;
            } else if (manhattan1 < manhattan2) {
                return -1;
            } else {
                return 0;
            }
        }
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

//        sb.append("hamming: " + hamming() + "\n");
//        sb.append("manhattan: " + manhattan() + "\n");
//        sb.append("goal?: " + isGoal() + "\n");

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("Starting....");

        int[][] input = { {8, 1, 3}, {4, 0, 2}, {7, 6, 5} };
        Board board = new Board(input);
        System.out.println(board);

        System.out.println("\nNeighbors:\n");
        for (Board b : board.neighbors()){
            System.out.println("\n" + b);
        }
    }

}
