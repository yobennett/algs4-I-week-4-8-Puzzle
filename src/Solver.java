import java.util.Comparator;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private SearchNode current;
    private static final int UNSOLVABLE = -1;
    private int moves = 0;
    private Queue<Board> solution;
    private MinPQ<SearchNode> pq;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.pq = new MinPQ<SearchNode>(new ByManhattanPriority());
        this.solution = new Queue<Board>();

        this.current = new SearchNode(initial, moves, null);

        this.pq.insert(current);
        System.out.println("* ENQUEUE");
        this.solution.enqueue(initial);

        printState();

        if (!initial.isGoal())
            solve(current);
    }

    private void solve(SearchNode node) {
        Board board = node.board();
        Board prev = (node.previousNode() != null) ? node.previousNode().board() : null;
        Iterable<Board> neighbors = board.neighbors();

        // add neighbors to priority queue
        for (Board neighbor : neighbors) {
            if (!neighbor.equals(prev)) {
                System.out.println("* ENQUEUE");
                pq.insert(new SearchNode(neighbor, moves, node));
            }
        }

        moves += 1;
        printState();

        System.out.println("** DEQUEUE");
        SearchNode min = pq.delMin();
        solution.enqueue(min.board());

        current = min;
        if (!current.board().isGoal()) {
            solve(current);
        }
    }

    private void printState() {
        StdOut.printf("Step %d:\n", moves);
        //System.out.printf("%-23s %s %f %n", "mean", "=", stats.mean());
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
        return solution.size();
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    private class SearchNode {

        private final Board board;
        private final int previousMoves;
        private final int manhattan;
        private final SearchNode previousNode;
        private final int priority;

        SearchNode(Board board, int previousMoves, SearchNode previousNode) {
            this.board = board;
            this.previousMoves = previousMoves;
            this.manhattan = board.manhattan();
            this.previousNode = previousNode;
            this.priority = manhattan + previousMoves;
        }

        public Board board() {
            return board;
        }

        public int priority() {
            return priority;
        }

        public SearchNode previousNode() {
            return previousNode;
        }

        @Override
        public String toString() {
            String result = "priority = " + priority;
            result += "\nmoves = " + previousMoves;
            result += "\nmanhattan = " + manhattan;
            result += "\n" + board.toString();
            return result;
        }

    }

    private class ByManhattanPriority implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode node1, SearchNode node2) {
            int priority1 = node1.priority();
            int priority2 = node2.priority();
            if (priority1 > priority2) {
                return 1;
            } else if (priority1 < priority2) {
                return -1;
            } else {
                return 0;
            }
        }
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
