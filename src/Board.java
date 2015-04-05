import java.util.Arrays;

/**
 * Created by bennett on 4/1/15.
 */
public class Board {

    private final int n;
    private final int[][] blocks;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] input) {
        if (input == null) {
            throw new NullPointerException();
        }

        this.n = input.length;
        this.blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(input[i], 0, this.blocks[i], 0, n);
        }
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
                if (!isBlank(i, j)) {
                    result += 1;
                }
            }
        }
        return result;
    }

    private boolean isBlank(int i, int j) {
        return blocks[i][j] == 0;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!isBlank(i, j)) {
                    result += distanceFromPlace(i, j);
                }
            }
        }
        return result;
    }

//    0-0 1
//    0-1 2
//    0-2 3
//    1-0 4
//    1-1 5
//    1-2 6
//    2-0 7
//    2-1 8
//    2-2 0
    private int distanceFromPlace(int i, int j) {
        int value = blocks[i][j];
        int goalRow = (int) Math.floor((value - 1) / n);
        int goalCol = (value - 1) % n;
        return Math.abs(goalRow - i) + Math.abs(goalCol - j);
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // a board that is obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        int[][] twinBlocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(blocks, 0, twinBlocks, 0, n);
        }
        for (int i = 0; i < 2; i++) {
            if (twinBlocks[i][0] != 0 && twinBlocks[i][1] != 0) {
                int tmp = twinBlocks[i][0];
                twinBlocks[i][0] = twinBlocks[i][1];
                twinBlocks[i][1] = tmp;
                break;
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
        return Arrays.deepEquals(this.blocks, that.blocks);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> result = new Stack<Board>();
        int[] blankCoordinates = coordinatesForBlank();
        int i = blankCoordinates[0];
        int j = blankCoordinates[1];

        // top
        if (withinBounds(i - 1, j)) {
            result.push(new Board(swap(i, j, i - 1, j)));
        }

        // right
        if (withinBounds(i, j + 1)) {
            result.push(new Board(swap(i, j, i, j + 1)));
        }

        // bottom
        if (withinBounds(i + 1, j)) {
            result.push(new Board(swap(i, j, i + 1, j)));
        }

        // left
        if (withinBounds(i, j - 1)) {
            result.push(new Board(swap(i, j, i, j - 1)));
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
//        sb.append("\nmanhattan=" + manhattan() + "\n");
        return sb.toString();
    }

    public static void main(String[] args) {

        int[][] input0 = { {1, 2, 3}, {4, 5, 6}, {7, 8, 0} };
        Board board0 = new Board(input0);
        System.out.println(board0);


//        1  2  3
//        0  7  6
//        5  4  8
        int[][] input = { {1, 2, 3}, {0, 7, 6}, {5, 4, 8} };
        Board board = new Board(input);
        System.out.println(board);

//        5  1  8
//        2  7  3
//        4  0  6
        int[][] input17 = { {5, 1, 8}, {2, 7, 3}, {4, 0, 6} };
        Board board17 = new Board(input17);
        System.out.println(board17);

//        5  8  7
//        1  4  6
//        3  0  2
        int[][] input27 = { {5, 8, 7}, {1, 4, 6}, {3, 0, 2} };
        Board board27 = new Board(input27);
        System.out.println(board27);

    }

}
