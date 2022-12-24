import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MergeX;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private LineSegment[] lineSegments;
    private int numberOfLineSegments;

    public BruteCollinearPoints(Point[] points) {
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

        // find all 4-point long line segments via brute force
        LineSegment[] tempLineSegments = new LineSegment[copy.length * copy.length];
        for (int i = 0; i < copy.length-3; i++) {
            for (int j = i+1; j < copy.length-2; j++) {
                for (int k = j+1; k < copy.length-1; k++) {
                    for (int m = k+1; m < copy.length; m++) {
                        if (copy[i].slopeTo(copy[j]) == copy[j].slopeTo(copy[k])
                        && copy[j].slopeTo(copy[k]) == copy[k].slopeTo(copy[m])) {
                            tempLineSegments[numberOfLineSegments] = new LineSegment(copy[i], copy[m]);
                            numberOfLineSegments++;
                        }
                    }
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
