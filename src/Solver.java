import java.util.Comparator;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private static final int UNSOLVABLE = -1;
    private final Board initial;
    private Queue<Board> solution;
    private MinPQ<SearchNode> minPQ;
    private int moves = 0;
    private int enqueues = 0;
    private int dequeues = 0;
    private boolean solvable = false;
    private SearchNode finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.initial = initial;
        this.minPQ = new MinPQ<SearchNode>(new ByManhattanPriority());
        this.solution = new Queue<Board>();

        enqueueSearchNode(new SearchNode(initial, 0, null));
        solve();
    }

    private void solve() {
        while (!isSolvable()) {
            SearchNode node = dequeueMinSearchNode();
            if (node.board.isGoal()) {
                solvable = true;
                finalNode = node;
            } else {
                for (Board neighbor : node.board.neighbors()) {
                    if (node.previousNode != null) {
                        if (!neighbor.equals(node.previousNode.board))
                            enqueueSearchNode(new SearchNode(neighbor, node.moves + 1, node));
                    } else {
                        enqueueSearchNode(new SearchNode(neighbor, node.moves + 1, node));
                    }
                }
            }

        }
    }


    private void enqueueSearchNode(SearchNode node) {
//        System.out.println("* ENQUEUE");
        enqueues++;
        minPQ.insert(node);
    }

    private SearchNode dequeueMinSearchNode() {
//        System.out.println("** DEQUEUE");
        dequeues++;
        return minPQ.delMin();
    }

    private void printState() {
        StdOut.printf("Step %d:\n", moves());
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (isSolvable() && finalNode != null) {
            return finalNode.moves;
        }
        return UNSOLVABLE;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable())
            return null;
        else {
            Stack<Board> result = new Stack<Board>();
            SearchNode node = finalNode;
            while (node != null) {
                result.push(node.board);
                node = node.previousNode;
            }
            moves = result.size();
            return result;
        }
    }

    private class SearchNode {

        private final Board board;
        private final int moves;
        private final int manhattan;
        private final int hamming;
        private final SearchNode previousNode;

        SearchNode(Board board, int moves, SearchNode previousNode) {
            this.board = board;
            this.moves = moves;
            this.manhattan = board.manhattan();
            this.hamming = board.hamming();
            this.previousNode = previousNode;
        }

        public Board board() {
            return board;
        }

        public int priority() {
            return manhattan + moves;
        }

        public int hammingPriority() {
            return hamming + moves;
        }

        public SearchNode previousNode() {
            return previousNode;
        }

        @Override
        public String toString() {
            String result = "priority = " + priority();
            result += "\nmoves = " + moves;
            result += "\nmanhattan = " + manhattan;
            result += "\nhamming = " + hamming;
            result += "\n" + board.toString();
            return result;
        }

    }

    private class ByManhattanPriority implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode node1, SearchNode node2) {
            return node1.priority() - node2.priority();
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
//            StdOut.println("enqueues = " + solver.enqueues + ", dequeues = " + solver.dequeues);
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
