import java.util.Comparator;

/**
 * Created by bennett on 4/2/15.
 */
public class Solver {

    private static final int UNSOLVABLE = -1;
    private MinPQ<Node> pqPrimary;
    private boolean solvable = false;
    private MinPQ<Node> pqTwin;
    private boolean solvableTwin = false;
    private Node finalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null)
            throw new NullPointerException();
        this.pqPrimary = new MinPQ<Node>(new ByManhattanPriority());
        this.pqTwin = new MinPQ<Node>(new ByManhattanPriority());

        enqueueSearchNode(pqPrimary, new Node(initial, 0, null));
        enqueueSearchNode(pqTwin, new Node(initial.twin(), 0, null));
        solve();
    }

    private void solve() {
        while (!isSolvable() && !isSolvableTwin()) {
            Node node = pqPrimary.delMin();
            Node nodeTwin = pqTwin.delMin();

            if (node.previousNode != null)
                assert node.priority() >= node.previousNode.priority()
                    : "Decreasing priority .\n"
                      + "curr:\n" + node
                      + "\nprev:\n" + node.previousNode;

            if (node.board.isGoal()) {
                solvable = true;
                finalNode = node;
            } else if (nodeTwin.board.isGoal()) {
                solvableTwin = true;
            } else {
                enqueueNeighbors(pqPrimary, node);
                enqueueNeighbors(pqTwin, nodeTwin);
            }
        }
    }

    private void enqueueNeighbors(MinPQ<Node> queue, Node node) {
        for (Board neighbor : node.board.neighbors()) {
            Node neighborNode = new Node(
                    neighbor,
                    node.moves + 1,
                    node);
            if (node.previousNode != null) {
                if (!neighbor.equals(node.previousNode.board))
                    enqueueSearchNode(queue, neighborNode);
            } else {
                enqueueSearchNode(queue, neighborNode);
            }
        }
    }


    private void enqueueSearchNode(MinPQ<Node> queue, Node node) {
        queue.insert(node);
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return solvable;
    }

    // is twin of the initial board solvable?
    private boolean isSolvableTwin() {
        return solvableTwin;
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
            Node node = finalNode;
            while (node != null) {
                result.push(node.board);
                node = node.previousNode;
            }
            return result;
        }
    }

    private class Node {

        private final Board board;
        private final int moves;
        private final int manhattan;
        private final int hamming;
        private final Node previousNode;

        Node(Board board, int moves, Node previousNode) {
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

        @Override
        public String toString() {
            String result = "priority = " + priority();
            result += "\nmoves = " + moves;
            result += "\nmanhattan = " + manhattan;
            result += "\n" + board.toString();
            return result;
        }

    }

    private class ByManhattanPriority implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
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
