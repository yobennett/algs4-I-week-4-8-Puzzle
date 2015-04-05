import java.util.Comparator;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private static final int UNSOLVABLE = -1;
    private MinPQ<SearchNode> minPQ;
    private boolean solvable = false;
    private SearchNode finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.minPQ = new MinPQ<SearchNode>(new ByManhattanPriority());

        enqueueSearchNode(new SearchNode(initial, 0, null));
        solve();
    }

    private void solve() {
        while (!isSolvable()) {
            SearchNode node = dequeueMinSearchNode();

            if (node.previousNode != null)
                assert node.priority() >= node.previousNode.priority()
                    : "Decreasing priority .\n"
                      + "curr:\n" + node
                      + "\nprev:\n" + node.previousNode;

            if (node.board.isGoal()) {
                solvable = true;
                finalNode = node;
            } else {
                for (Board neighbor : node.board.neighbors()) {
                    SearchNode neighborNode = new SearchNode(
                        neighbor,
                        node.moves + 1,
                        node);
                    if (node.previousNode != null) {
                        if (!neighbor.equals(node.previousNode.board))
                            enqueueSearchNode(neighborNode);
                    } else {
                        enqueueSearchNode(neighborNode);
                    }
                }
            }
        }
    }


    private void enqueueSearchNode(SearchNode node) {
        minPQ.insert(node);
    }

    private SearchNode dequeueMinSearchNode() {
        return minPQ.delMin();
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
            result += "\n" + board.toString();
            return result;
        }

    }

    private class ByManhattanPriority implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode node1, SearchNode node2) {
            if (node1.priority() > node2.priority()) {
                return 1;
            } else if (node1.priority() < node2.priority()) {
                return -1;
            } else {
                return node1.manhattan - node2.manhattan;
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
