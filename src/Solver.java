import java.util.ArrayList;
import java.util.List;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private Board previous;
    private Board current;
    private static final int UNSOLVABLE = -1;
    private int moves = 0;
    private List<Board> solution;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.current = initial;
        this.previous = null;
        this.solution = new ArrayList<Board>();
        addMove(initial);

        if (!current.isGoal()) {
            solve(current, previous);
        }
    }

    private void addMove(Board board) {
        solution.add(board);
        moves += 1;
    }

    private void solve(Board currentBoard, Board previousBoard) {
        MinPQ<Board> pq = new MinPQ<Board>(currentBoard.MANHATTAN_PRIORITY);
        Iterable<Board> neighbors = currentBoard.neighbors();

        // add neighbors to MinPQ, sort by Manhattan priority
        for (Board neighbor : neighbors) {
            if (!neighbor.equals(previousBoard)) {
                pq.insert(neighbor);
            }
        }

        Board min = pq.delMin();
        addMove(min);

        previous = current;
        current = min;
        if (!current.isGoal()) {
            solve(current, previous);
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return true;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) {
            return UNSOLVABLE;
        }
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
