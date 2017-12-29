import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

  private double[] thresholds;
  private int experiments;
  private int size;
  private double mean,stddev;

  // perform T independent computational experiments on an N-by-N grid
  public PercolationStats(int N, int T) {
    if (N <= 0) throw new java.lang.IllegalArgumentException("N is out of bounds");
    if (T <= 0) throw new java.lang.IllegalArgumentException("T is out of bounds");

    size = N;
    experiments = T;
    thresholds = new double[experiments];

    for (int i=0; i<T; i++) {
      thresholds[i] = findPercolationThreshold();
    }
    mean = this.mean();
    stddev = this.stddev();
  } 

  // sample mean of percolation threshold
  public double mean() {
    return StdStats.mean(thresholds);
  }

  // sample standard deviation of percolation threshold
  public double stddev() {
    if (experiments == 1) return Double.NaN;
    return StdStats.stddev(thresholds);
  }

  // returns lower bound of the 95% confidence interval
  public double confidenceLo() {
    return this.mean - 1.96*this.stddev/Math.sqrt(experiments);
  }

  // returns upper bound of the 95% confidence interval
  public double confidenceHi() {
    return this.mean + 1.96*this.stddev/Math.sqrt(experiments);
  }
  
  private double findPercolationThreshold() {
    Percolation perc = new Percolation(size);
    int i, j;
    int count = 0;
    while (!perc.percolates()) {
      do {
        i = StdRandom.uniform(size) + 1;
        j = StdRandom.uniform(size) + 1;
      } while (perc.isOpen(i,j));
      count++;
      perc.open(i, j);
    }
    return count/(Math.pow(size,2));
  }
    
  // test client, described below
  public static void main(String[] args) {
    int N = Integer.parseInt(args[0]);
    int T = Integer.parseInt(args[1]);
    PercolationStats stats = new PercolationStats(N, T);
    StdOut.printf("mean = %f\n", stats.mean);
    StdOut.printf("stddev = %f\n", stats.stddev);
    StdOut.printf("95%% confidence interval = %f, %f\n", stats.confidenceLo(), stats.confidenceHi());
  }
}