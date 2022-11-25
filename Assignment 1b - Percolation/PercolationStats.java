import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private double[] trialLog;
    private int trials;
    
    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }

        this.trials = trials;
        trialLog = new double[trials]; // stores fraction of open sites on percolation for each trial

        // conduct trials
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                int row = StdRandom.uniformInt(n)+1;
                int col = StdRandom.uniformInt(n)+1;
                percolation.open(row, col);
            }
            trialLog[i] = percolation.numberOfOpenSites()/Math.pow(n, 2);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(trialLog);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(trialLog);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (CONFIDENCE_95*stddev())/Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (CONFIDENCE_95*stddev())/Math.sqrt(trials);
    }

   // test client (see below)
   public static void main(String[] args) {
    int gridSize = Integer.parseInt(args[0]);
    int numberOfTrials = Integer.parseInt(args[1]);

    PercolationStats trial = new PercolationStats(gridSize, numberOfTrials);
    
    StdOut.println("mean = " + trial.mean());
    StdOut.println("stddev = " + trial.stddev());
    StdOut.println("95% confidence interval = [" + trial.confidenceLo() + ", " + trial.confidenceHi() + "]");
   }

}