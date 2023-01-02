import java.util.ArrayList;
import java.util.Comparator;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private SearchNode solutionNode;
    private boolean isSolvable;
    private final Comparator<SearchNode> MANHATTAN_COMPARATOR = new ManhattanPriority();

    public Solver(Board initial) {

        // initialize search queues
        MinPQ<SearchNode> queue = new MinPQ<SearchNode>(MANHATTAN_COMPARATOR);
        SearchNode initialNode = new SearchNode(initial, null, 0);
        queue.insert(initialNode);

        MinPQ<SearchNode> twinQueue = new MinPQ<SearchNode>(MANHATTAN_COMPARATOR);
        SearchNode initialTwinNode = new SearchNode(initial.twin(), null, 0);
        twinQueue.insert(initialTwinNode);

        // A* search
        while (!queue.isEmpty()) {
            // search
            SearchNode dequeued = queue.delMin();
            if (dequeued.board.isGoal()) {
                solutionNode = dequeued;
                isSolvable = true;
                return;
            };
            for (Board neighbor : dequeued.board.neighbors()) {
                if (dequeued.previousNode != null && neighbor.equals(dequeued.previousNode.board)) continue; // check if neighbor is a neighbor of previous node
                SearchNode newNode = new SearchNode(neighbor, dequeued, dequeued.moves + 1);
                queue.insert(newNode);
            }
            
            // twin search
            SearchNode twinDequeued = twinQueue.delMin();
            if (twinDequeued.board.isGoal()) {
                isSolvable = false;
                return;
            }
            for (Board neighbor : twinDequeued.board.neighbors()) {
                if (neighbor.equals(twinDequeued.board)) continue; // check if neighbor is previous node
                SearchNode newNode = new SearchNode(neighbor, twinDequeued, twinDequeued.moves + 1);
                twinQueue.insert(newNode);
            }
        }
    }

    private class ManhattanPriority implements Comparator<SearchNode>
    {
        private int manhattanPriority(SearchNode node) {
            return node.board.manhattan() + node.moves;
        }

        public int compare(SearchNode searchNode1, SearchNode searchNode2) {
            if (manhattanPriority(searchNode1) < manhattanPriority(searchNode2)) return -1;
            if (manhattanPriority(searchNode1) > manhattanPriority(searchNode2)) return 1;
            else return 0;
        }
    }

    private class SearchNode
    {
        private Board board;
        private int moves;
        private SearchNode previousNode;

        public SearchNode(Board board, SearchNode previousNode, int moves) {
            this.board = board;
            this.moves = moves;
            this.previousNode = previousNode;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return solutionNode.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;

        // reconstruct path from solution to initial
        ArrayList<Board> movesToSolutionReversed = new ArrayList<Board>();
        SearchNode current = solutionNode;
        for (int i = 0; i <= solutionNode.moves + 1; i ++) {
            if (current.previousNode == null) {
                movesToSolutionReversed.add(current.board);
                break;
            } 
            movesToSolutionReversed.add(current.board);
            current = current.previousNode;
        }

        // reverse the order
        ArrayList<Board> movesToSolution = new ArrayList<Board>();
        for (int i = movesToSolutionReversed.size() - 1; i >=0; i--) {
            Board board = movesToSolutionReversed.get(i);
            movesToSolution.add(board);
        }

        return movesToSolution;
    }

    // test client (see below) 
    public static void main(String[] args) {

        // create initial board from file
        // In in = new In(args[0]);
        // int n = in.readInt();
        // int[][] tiles = new int[n][n];
        // for (int i = 0; i < n; i++)
        //     for (int j = 0; j < n; j++)
        //         tiles[i][j] = in.readInt();

        int[][] tiles = {{6, 5, 3}, {4, 1, 7}, {0, 2, 8}};
        // int[][] tiles = {{0, 1}, {2, 3}};
        Board initial = new Board(tiles);
    
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