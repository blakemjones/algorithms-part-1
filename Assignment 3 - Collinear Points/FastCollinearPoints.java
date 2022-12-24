import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MergeX;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private LineSegment[] lineSegments;
    private int numberOfLineSegments;

    public FastCollinearPoints(Point[] points) {
        // null checks
        if (points == null) throw new IllegalArgumentException();
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException();
        }

        // copy input to avoid input mutation
        Point[] copy = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            copy[i] = points[i];
        }

        // sort and check for duplicates
        MergeX.sort(copy);
        for (int i = 0; i < copy.length-1; i++) {
            if (copy[i].compareTo(copy[i+1]) == 0) throw new IllegalArgumentException();
        }

        // iterate through points to find collinear sets
        LineSegment[] tempLineSegments = new LineSegment[points.length * points.length];
        for (int i = 0; i < copy.length; i++) {
            Point origin = copy[i];
            MergeX.sort(copy, origin.slopeOrder()); 

            // check for adjacent array entries containing equivalent slopes with origin
            int head = 0;
            for (int j = 0; j < copy.length; j++) {
                if (origin.slopeTo(copy[head]) != origin.slopeTo(copy[j])) {
                    // construct line segment if greater than 3 points
                    if (j - head > 2 && origin.compareTo(copy[head]) < 0) {
                        tempLineSegments[numberOfLineSegments] = new LineSegment(origin, copy[j-1]);
                        numberOfLineSegments++;
                    }
                    head = j;
                }

                // check for instance where collinear point set extends to end of array
                if ((j == copy.length - 1) && (origin.slopeTo(copy[head]) == origin.slopeTo(copy[j])) 
                && (j - head > 2) && origin.compareTo(copy[head]) < 0) {
                        tempLineSegments[numberOfLineSegments] = new LineSegment(origin, copy[j]);
                        numberOfLineSegments++;
                    }
                }
            }

        // copy line segments to final line segment object
        this.lineSegments = new LineSegment[numberOfLineSegments];
        for (int i = 0; i < numberOfLineSegments; i++) {
            this.lineSegments[i] = tempLineSegments[i];
        }
    }

    public int numberOfSegments() {
        return numberOfLineSegments;
    }

    public LineSegment[] segments() {
        LineSegment[] tempLineSegments = new LineSegment[this.lineSegments.length];
        for (int i = 0; i < this.lineSegments.length; i++) {
            tempLineSegments[i] = this.lineSegments[i];
        }
        return tempLineSegments;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }
    
        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
    
        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
