import java.util.ArrayList;
import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private Board initial;
    private Board initialTwin;
    private ArrayList<Board> movesToSolution;
    private ArrayList<Board> twinMovesToSolution;
    private boolean isSolvable;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        // initialize solver
        this.initial = initial;
        this.movesToSolution = new ArrayList<Board>();
        this.initialTwin = initial.twin();
        this.twinMovesToSolution = new ArrayList<Board>();

        // initialize search queues
        Comparator<SearchNode> comparator = new ManhattanPriority();
        MinPQ<SearchNode> queue = initializeQueue(initial, comparator);
        MinPQ<SearchNode> twinQueue = initializeQueue(initialTwin, comparator);

        // initialize search
        SearchNode dequeued = initializeSearch(queue, movesToSolution);
        SearchNode twinDequeued = initializeSearch(twinQueue, twinMovesToSolution);

        // A* search
        while (!dequeued.board.isGoal()) {
            // search
            for (Board neighbor : dequeued.board.neighbors()) {
                if (neighbor.equals(dequeued.board)) continue; // check if neighbor is previous node
                SearchNode newNode = new SearchNode(neighbor, dequeued.moves + 1);
                queue.insert(newNode);
            }
            dequeued = queue.delMin(); // find board closest to solution
            movesToSolution.add(dequeued.board);

            // twin search
            if (twinDequeued.board.isGoal()) {
                isSolvable = false;
                return;
            }
            for (Board neighbor : twinDequeued.board.neighbors()) {
                if (neighbor.equals(twinDequeued.board)) continue; // check if neighbor is previous node
                SearchNode newNode = new SearchNode(neighbor, twinDequeued.moves + 1);
                twinQueue.insert(newNode);
            }
            twinDequeued = twinQueue.delMin(); // find board closest to solution
        }
        isSolvable = true;
    }

    private MinPQ<SearchNode> initializeQueue(Board board, Comparator<SearchNode> comparator) {
        MinPQ<SearchNode> queue = new MinPQ<SearchNode>(comparator);
        SearchNode initial = new SearchNode(board, 0);
        queue.insert(initial);
        return queue;
    }

    private SearchNode initializeSearch(MinPQ<SearchNode> queue, ArrayList<Board> movesToSolution) {
        SearchNode dequeued = queue.delMin();
        movesToSolution.add(dequeued.board);
        return dequeued;
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

        public SearchNode(Board board, int moves) {
            this.board = board;
            this.moves = moves;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return initial.hamming();
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return this.movesToSolution;
    }

    // test client (see below) 
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
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