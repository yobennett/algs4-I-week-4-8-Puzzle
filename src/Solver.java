import java.util.Comparator;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private static final int UNSOLVABLE = -1;
    private final Board initial;
    private Queue<Board> solution;
    private MinPQ<SearchNode> pq;
    private int enqueues = 0;
    private int dequeues = 0;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.initial = initial;
        this.pq = new MinPQ<SearchNode>(new ByManhattanPriority());
        this.solution = new Queue<Board>();
        solve();
    }

    private void enqueueSearchNode(SearchNode node) {
//        System.out.println("* ENQUEUE");
        enqueues++;
        pq.insert(node);
    }

    private SearchNode dequeueMinSearchNode() {
//        System.out.println("** DEQUEUE");
        dequeues++;
        return pq.delMin();
    }

    private void addSolutionStep(Board board) {
        solution.enqueue(board);
    }

    private void solve() {
        SearchNode node = new SearchNode(initial, 0, null);

        enqueueSearchNode(node);

        SearchNode min = dequeueMinSearchNode();
        addSolutionStep(min.board());

        if (!min.board().isGoal()) {
            solve(min);
        }
    }

    private void solve(SearchNode node) {
        Board board = node.board();
        Board prev = (node.previousNode() != null) ? node.previousNode().board() : null;

        // add neighbors to priority queue
        Iterable<Board> neighbors = board.neighbors();
        for (Board neighbor : neighbors) {
            if (!neighbor.equals(prev)) {
                enqueueSearchNode(new SearchNode(neighbor, moves(), node));
            }
        }

//        printState();

        SearchNode min = dequeueMinSearchNode();
        addSolutionStep(min.board());

        if (!min.board().isGoal()) {
            solve(min);
        }
    }

    private void printState() {
        StdOut.printf("Step %d:\n", moves());
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
        return solution.size() - 1;
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
            StdOut.println("enqueues = " + solver.enqueues + ", dequeues = " + solver.dequeues);
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
