import java.util.ArrayList;

import edu.princeton.cs.algs4.StdOut;

public class Board {
    private int[][] tiles;
    private int blankTileRow;
    private int blankTileCol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.tiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (tiles[i][j] == 0) {
                    blankTileRow = i;
                    blankTileCol = j;
                }
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }
                                           
    // string representation of this board
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(tiles.length); result.append("\n");
        for (int i = 0; i < tiles.length; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < tiles.length; j++) {
                row.append(tiles[i][j]); row.append(" ");
            }
            result.append(row.toString()); result.append("\n");
        }
        return result.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                int tileValue = tiles[i][j];
                if (tileValue == 0) tileValue = 9; // blank tile should be in right-bottommost square
                if (tileValue != (i * tiles.length) + j + 1) hamming++;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                int tileValue = tiles[i][j];
                if (tileValue == 0) tileValue = 9; // blank tile should be in right-bottommost square
                int expectedRow = (tileValue - 1) / tiles.length;
                int expectedCol = (tileValue - 1) % tiles.length;
                manhattan += Math.abs(expectedRow - i) + Math.abs(expectedCol - j);
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        return this.toString().equals(y.toString());
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<Board>();
        if (blankTileRow - 1 > 0) {
            Board neighboringBoard = new Board(tiles);
            neighboringBoard.tiles[blankTileRow][blankTileCol] = neighboringBoard.tiles[blankTileRow - 1][blankTileCol];
            neighboringBoard.tiles[blankTileRow - 1][blankTileCol] = 0;
            neighboringBoard.blankTileRow--;
            neighbors.add(neighboringBoard);
        }
        if (blankTileRow + 1 < tiles.length) {
            Board neighboringBoard = new Board(tiles);
            neighboringBoard.tiles[blankTileRow][blankTileCol] = neighboringBoard.tiles[blankTileRow + 1][blankTileCol];
            neighboringBoard.tiles[blankTileRow + 1][blankTileCol] = 0;
            neighboringBoard.blankTileRow++;
            neighbors.add(neighboringBoard);
        }
        if (blankTileCol - 1 > 0) {
            Board neighboringBoard = new Board(tiles);
            neighboringBoard.tiles[blankTileRow][blankTileCol] = neighboringBoard.tiles[blankTileRow][blankTileCol - 1];
            neighboringBoard.tiles[blankTileRow][blankTileCol - 1] = 0;
            neighboringBoard.blankTileCol--;
            neighbors.add(neighboringBoard);
        }
        if (blankTileCol + 1 < tiles.length) {
            Board neighboringBoard = new Board(tiles);
            neighboringBoard.tiles[blankTileRow][blankTileCol] = neighboringBoard.tiles[blankTileRow][blankTileCol + 1];
            neighboringBoard.tiles[blankTileRow][blankTileCol + 1] = 0;
            neighboringBoard.blankTileCol++;
            neighbors.add(neighboringBoard);
        }
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board twinBoard = new Board(this.tiles);

        // if 1st tile is blank, swap 2nd and 3rd tile
        if (twinBoard.tiles[0][0] == 0) {
            if (twinBoard.dimension() > 2) {
                int temp = twinBoard.tiles[0][1];
                twinBoard.tiles[0][1] = twinBoard.tiles[0][2];
                twinBoard.tiles[0][2] = temp;
            }
            else {
                int temp = twinBoard.tiles[0][1];
                twinBoard.tiles[0][1] = twinBoard.tiles[1][0];
                twinBoard.tiles[1][0] = temp;
            }
        }

        // if 2nd tile is blank, swap 1st and 3rd tile
        else if (twinBoard.tiles[0][1] == 0) {
            if (twinBoard.dimension() > 2) {
                int temp = twinBoard.tiles[0][0];
                twinBoard.tiles[0][0] = twinBoard.tiles[0][2];
                twinBoard.tiles[0][2] = temp;
            }
            else {
                int temp = twinBoard.tiles[0][0];
                twinBoard.tiles[0][0] = twinBoard.tiles[1][0];
                twinBoard.tiles[1][0] = temp;
            }
        }

        // else neither 1st nor 2nd tiles are blank, swap these
        else {
            int temp = twinBoard.tiles[0][0];
            twinBoard.tiles[0][0] = twinBoard.tiles[0][1];
            twinBoard.tiles[0][1] = temp;
        }

        return twinBoard;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int [][] testTiles = {{1, 2, 3},{4, 5, 6},{7, 8, 0}};
        Board testBoard = new Board(testTiles);
        StdOut.println(testBoard.toString());
        
        for (Board board : testBoard.neighbors()) {
            StdOut.println(board.toString());
            StdOut.println("Hamming distance: " + board.hamming());
            StdOut.println("Manhattan distance: " + board.manhattan());
        }

        int [][] manhattanTest = {{0, 2, 3},{4, 5, 6},{7, 8, 1}};
        Board manhattanTestBoard = new Board(manhattanTest);
        StdOut.println("Maximum manhattan distance: " + manhattanTestBoard.manhattan());

        int [][] twinTest = {{1, 2, 0},{4, 5, 6},{7, 8, 3}};
        Board twinTestBoard = new Board(twinTest);
        StdOut.println(twinTestBoard.twin().toString());

        int [][] twin2x2Test = {{1, 0},{3, 2}};
        Board twin2x2TestBoard = new Board(twin2x2Test);
        StdOut.println(twin2x2TestBoard.twin().toString());
    }
}