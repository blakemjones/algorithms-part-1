import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    private boolean[][] grid;
    private WeightedQuickUnionUF uF;
    private WeightedQuickUnionUF bottomlessUf;
    private int numberOfOpenSites = 0;
    private int topArtificalNodeIndex;
    private int bottomArtificalNodeIndex;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException();
        
        // Grid stores the sites as a series of booleans indicating if it is open
        grid = new boolean[n][n];

        // instantiate all sites as blocked
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }

        int numberOfNodes = (int) Math.pow(n, 2);
        topArtificalNodeIndex = numberOfNodes;
        bottomArtificalNodeIndex = numberOfNodes + 1;

        // UF instantiates the efficient UnionFind class. Includes two artificial nodes where the top is index n^2 and the bottom is index n^2+1
        uF = new WeightedQuickUnionUF(numberOfNodes+2);
        bottomlessUf = new WeightedQuickUnionUF(numberOfNodes+1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row <= 0 || col <= 0 || row > grid.length || col > grid.length) throw new IllegalArgumentException();

        // check if site has already been opened
        if (grid[row-1][col-1]) {
            return;
        }

        // open site in grid data
        grid[row-1][col-1] = true;
        numberOfOpenSites++;

        // open site in UF data
        int originUfIndex = calculateUfIndex(row, col);
        if (row - 1 > 0 && grid[row-2][col-1]) {
            bottomlessUf.union(originUfIndex, calculateUfIndex(row - 1, col));
            uF.union(originUfIndex, calculateUfIndex(row - 1, col));
        } 
        if (col + 1 <= grid.length && grid[row-1][col]) {
            bottomlessUf.union(originUfIndex, calculateUfIndex(row, col + 1));
            uF.union(originUfIndex, calculateUfIndex(row, col + 1));
        }  
        if (row + 1 <= grid.length && grid[row][col-1]) {
            bottomlessUf.union(originUfIndex, calculateUfIndex(row + 1, col));
            uF.union(originUfIndex, calculateUfIndex(row + 1, col));
        }
        if (col - 1 > 0 && grid[row-1][col-2]) {
            bottomlessUf.union(originUfIndex, calculateUfIndex(row, col - 1));
            uF.union(originUfIndex, calculateUfIndex(row, col - 1));
        }
        
        // if origin node is at the top and opens, connect to artificial node
        if (row == 1) {
            bottomlessUf.union(originUfIndex, topArtificalNodeIndex);
            uF.union(originUfIndex, topArtificalNodeIndex);
        } 
        
        // if origin node is at the bottom and percolates, connect to artificial node
        if (row == grid.length) {
            uF.union(originUfIndex, bottomArtificalNodeIndex);
        } 
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row <= 0 || col <= 0 || row > grid.length || col > grid.length) {
            throw new IllegalArgumentException();
        }
        return grid[row-1][col-1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row <= 0 || col <= 0 || row > grid.length || col > grid.length) {
            throw new IllegalArgumentException();
        }
        return bottomlessUf.find(topArtificalNodeIndex) == bottomlessUf.find(calculateUfIndex(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return uF.find(topArtificalNodeIndex) == uF.find(bottomArtificalNodeIndex);
    }

    // test client (optional)
    public static void main(String[] args) {
        // construct test percolation instance
        Percolation percolation = null;
        int n = StdIn.readInt();
        percolation = new Percolation(n);
        percolation.printGrid();


        // open all sites test
        for (int i = 1; i <= percolation.grid.length; i++) {
            for (int j = 1; j <= percolation.grid.length; j++) {
                percolation.open(i, j);
            }
        }
        percolation.printGrid();
        if (percolation.percolates()) {
            StdOut.println("Percolates!");
        }
        else {
            StdOut.println("Does not percolate, something is wrong.");
        }

        // open 1 site test
        percolation = new Percolation(n);
        percolation.open(1, 1);
        if (!percolation.percolates()) {
            StdOut.println("Does not percolate with one site open, success!");
        }
        percolation.open(2, 1);
        percolation.open(3, 1);
        if (percolation.percolates()) {
            StdOut.println("Percolates with one column open, success!");
        }

        // check that no full sites exist without being open
        percolation = new Percolation(n);
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (!percolation.isOpen(i, j) == percolation.isFull(i, j)) {
                    StdOut.println("Full site exists without being open, FAILURE!");
                }
            }
        }
    }

    private void printGrid() {
        
        for (int i = 0; i < grid.length; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < grid.length; j++) {
                row.append(grid[i][j]);
            }
            StdOut.println(row);
        }
        StdOut.print("\n");
    }

    private int calculateUfIndex(int row, int col) {
        int ufIndex = grid.length*(row-1) + col - 1; // subtract 1 to adjust for indexing
        return ufIndex;
    }
}