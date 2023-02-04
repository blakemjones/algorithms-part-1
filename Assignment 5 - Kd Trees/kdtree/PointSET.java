import java.util.ArrayList;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> points;

    // construct an empty set of points 
    public PointSET()   {
        points = new SET<Point2D>();
    }           
    
    // is the set empty? 
    public boolean isEmpty() {
        return points.isEmpty();
    }                     

    // number of points in the set 
    public int size() {
        return points.size();
    }                  

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return points.contains(p);
    }
    
    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }                          

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        ArrayList<Point2D> pointsInRect = new ArrayList<Point2D>();
        for (Point2D p : points) {
            if (p.x() >= rect.xmin() && p.x() <= rect.xmax() && p.y() >= rect.ymin() && p.y() <= rect.ymax()) pointsInRect.add(p);
        }
        return pointsInRect;
    }             

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (points.isEmpty()) return null;
        
        Point2D nearestNeighbor = points.max();
        for (Point2D pointInSet : points) {
            if (p.distanceTo(pointInSet) < p.distanceTo(nearestNeighbor)) nearestNeighbor = pointInSet;
        }
        return nearestNeighbor;
    }             

    // unit testing of the methods (optional) 
    public static void main(String[] args) {
    }
 }