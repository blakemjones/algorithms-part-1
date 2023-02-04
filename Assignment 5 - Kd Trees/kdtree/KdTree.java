import java.util.ArrayList;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    // construct an empty set of points 
    public KdTree()   {
        root = null;
    }           

    private Node root;

    // used as global variables to aid in implementation of insert()
    // - prevents need to keep track of parent node
    private int depth; 
    private double collisionLine;

    private class Node {
        private Point2D point;
        private Node left, right;
        private int depth;
        private int size;

        // this is the value of the splitting line of its parent (the constant line x = collisionLine or y = collisionLine with respect to the node's depth)
        private double collisionLine;
        private Node(Point2D point, int depth, int size, double collisionLine) 
        { this.point = point; this.depth = depth; this.size = size; this.collisionLine = collisionLine; }

        // constructs a rectangle with respect to this point and it's left child's point
        private RectHV leftRect() {
            double xmin; double ymin; double xmax; double ymax;
            if (left == null) return null;

            Point2D child = left.point;
            if (depth % 2 == 1) {
                if (collisionLine > point.y()) {
                    xmin = child.x(); xmax = point.x(); ymax = collisionLine;
                    ymin = Math.min(child.y(), point.y());
                }
                else {
                    xmin = child.x(); xmax = point.x(); ymin = collisionLine; 
                    ymax = Math.max(child.y(), point.y());
                }
            }
            else {
                if (collisionLine > point.x()) {
                    ymin = point.y(); xmax = collisionLine; ymax = child.y();
                    xmin = Math.min(child.x(), point.x());
                }
                else {
                    xmin = collisionLine; ymin = point.y(); ymax = child.y();
                    xmax = Math.min(child.x(), point.x());
                }
            }
            return new RectHV(xmin, ymin, xmax, ymax);
        }

        private RectHV rightRect() {
            double xmin; double ymin; double xmax; double ymax;
            if (left == null) return null;

            Point2D child = right.point;
            if (depth % 2 == 1) {
                if (collisionLine > point.y()) {
                    xmin = point.x(); xmax = child.x(); ymax = collisionLine;
                    ymin = child.y() < point.y() ? child.y() : point.y();
                }
                else {
                    xmin = point.x(); xmax = child.x(); ymin = collisionLine; 
                    ymax = child.y() < point.y() ? point.y() : child.y();
                }
            }
            else {
                if (collisionLine > point.x()) {
                    ymin = child.y(); xmax = collisionLine; ymax = point.y();
                    xmin = Math.min(child.x(), point.x());
                }
                else {
                    xmin = collisionLine; ymin = child.y(); ymax = point.y();
                    xmax = Math.min(child.x(), point.x());
                }
            }
            return new RectHV(xmin, ymin, xmax, ymax);
        }
    }
    
    // is the set empty? 
    public boolean isEmpty() {
        return root == null;
    }                     

    // number of points in the set 
    public int size() {
        return size(root);
    }            
    
    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        currDepth = 1;
        collisionLine = Double.POSITIVE_INFINITY;
        root = insert(root, p);
    }
    
    // does the set contain point p?
    public boolean contains(Point2D p) {
        return !(get(root, p) == null);
    }          

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    // all points that are inside the rectangle (or on the boundary) 
    public Iterable<Point2D> range(RectHV rect) {
        RangeSearch rangeSearch = new RangeSearch(rect, this);
        return rangeSearch.pointsInRange;
    }

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        NearestNeighborSearch nearestNeighborSearch = new NearestNeighborSearch(p, this);
        return nearestNeighborSearch.nearestNeighbor.point;
    }
    
    /* 
     * HELPER METHODS
    */
    // size() helper method
    private int size(Node node) {
        if (node == null) return 0;
        else return node.size;
    }
    //#endregion
    // draw() helper methods
    private Node draw(Node node) {
        if (node == null) return null;

        node.point.draw();

        if (node.left != null) node = draw(node.left);
        if (node.right != null) node = draw(node.right);

        return node;
    }
    // #endregion
    // insert() and contains() helper methods
    private Node insert(Node curr, Point2D p) {
        if (curr == null) return new Node(p, depth, 1, collisionLine);
        depth++;
        collisionLine = depth % 2 == 1 ? curr.point.y() : curr.point.x();
        
        boolean isLeft = isLeft(curr, p);
        if (isLeft) curr.left = insert(curr.left, p);
        else {
            if (curr.point.equals(p)) return curr; // duplicate check
            curr.right = insert(curr.right, p);
        }

        curr.size = size(curr.left) + size(curr.right) + 1;
        return curr;
    }

    private Node get(Node currNode, Point2D p) {
        if (currNode == null) return null;
        else if (currNode.point.equals(p)) return currNode;
        else {
            boolean isLeft = isLeft(currNode, p);
            if (isLeft) currNode = get(currNode.left, p);
            else currNode = get(currNode.right, p);
        }
        return currNode;
    }
    private boolean isLeft(Node node, Point2D point) 
    {
        boolean isLeft;
        if (node.depth % 2 == 1) isLeft = point.x() < node.point.x();
        else isLeft = point.y() < node.point.y();

        return isLeft;
    }
    // #endregion
    // range() helper class
    private class RangeSearch 
    {
        ArrayList<Point2D> pointsInRange;
        Stack<Node> nodesToVisit;
        RectHV rect;
        KdTree kdTree;
        private RangeSearch(RectHV rect, KdTree kdTree) 
        { 
            this.pointsInRange = new ArrayList<Point2D>();
            this.nodesToVisit = new Stack<Node>();
            this.rect = rect;
            this.kdTree = kdTree;
            search();
        }

        private void search() {
            search(kdTree.root);
        }

        private Node search(Node node) {
            if (node == null) {
                if (nodesToVisit.isEmpty()) {
                    return null;
                }
                node = nodesToVisit.pop();
            }

            if (intersectsSplit(node)) {
                if (inBounds(node)) pointsInRange.add(node.point);
                if (node.right != null) nodesToVisit.push(node.right);
                node = search(node.left);
                if (node == null)  return null;
            }

            if (strictlyLeft(node)) node = search(node.left);
            else { 
                node = search(node.right);
            }
            return node;
        }

        private boolean intersectsSplit(Node node) {
            Point2D p = node.point;
            if (node.depth % 2 == 1) return p.x() >= rect.xmin() && p.x() <= rect.xmax();
            else return p.y() >= rect.ymin() && p.y() <= rect.ymax();
        }

        private boolean inBounds(Node node) {
            Point2D p = node.point;
            return p.x() >= rect.xmin() && p.x() <= rect.xmax() && p.y() >= rect.ymin() && p.y() <= rect.ymax();
        }

        private boolean strictlyLeft(Node node) {
            Point2D p = node.point;
            if (node.depth % 2 == 1) return rect.xmax() < p.x();
            else return rect.ymax() < p.y();
        }
    }
    // #endregion
    // nearest() helper class
    private class NearestNeighborSearch 
    {
        Point2D query;
        Node nearestNeighbor;
        Stack<Node> nodesToVisit;
        KdTree kdTree;
        private NearestNeighborSearch(Point2D query, KdTree kdTree) 
        { 
            this.query = query;
            this.kdTree = kdTree;
            this.nearestNeighbor = kdTree.root;

            this.nodesToVisit = new Stack<Node>(); // keeps track of parts of tree that may contain closer point

            search();
        }

        private void search() {
            search(kdTree.root);
        }

        private Node search(Node curr) {
            if (curr == null) {
                if (nodesToVisit.isEmpty()) {
                    return null;
                }
                curr = nodesToVisit.pop();
            }

            if (isNearest(curr)) {
                nearestNeighbor = curr;
                prune();
            }

            if (isLeftOfQuery(curr)) {
                if (curr.right != null) nodesToVisit.push(curr.right);
                curr = search(curr.left);
            }
            else {
                if (curr.left != null) nodesToVisit.push(curr.left);
                curr = search(curr.right);
            }
            
            return curr;
        }

        private boolean isLeftOfQuery(Node node) {
            Point2D p = node.point;
            if (node.depth % 2 == 1) return query.x() < p.x();
            else return query.y() < p.y();
        }

        private boolean isNearest(Node node) {
            double currNearestDistance = query.distanceTo(nearestNeighbor.point);
            double candidateDistance = query.distanceTo(node.point);
            return candidateDistance < currNearestDistance;
        }

        private void prune() {
            // should prune if the next node to visit's adjacent rectangles can't contain a closer point
            Node next = nodesToVisit.peek();
            RectHV leftRect = next.leftRect();
            RectHV rightRect = next.rightRect();

            if (leftRect != null 
            && leftRect.distanceTo(query) > nearestNeighbor.point.distanceTo(query)) //prune

            if (rightRect != null 
            && rightRect.distanceTo(query) > nearestNeighbor.point.distanceTo(query)) //prune
            
            return;
        }
    }
    // #endregion
    
    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        
        StdOut.println("*****NOW PERFORMING CONSTRUCTOR TESTS*****");
        String testName = "Constructor initializes KdTree";
        KdTree test = new KdTree();
        boolean result = test.size() == 0 
        && test.root == null;
        test.printTestResult(testName, result);

        /* FOLLOWING UNIT TEST SETS */
        test.RunInsertionTests();
        test.RunContainsTests();
        test.RunRangeSearchTests();
        test.RunNearestNeighborTests();

        /* AUTO GRADER FEEDBACK DEBUGGING */
        // Size discrepancy - found that previous version was inserting duplicate points
        test = new KdTree();
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        points.add(new Point2D(1.0, 0.0));
        points.add(new Point2D(0.0, 0.0));
        points.add(new Point2D(1.0, 1.0));
        points.add(new Point2D(0.0, 0.0));
        for (Point2D point : points) {
            test.insert(point);
        }
        StdOut.println("Size of tree after inserting 4 points is: " + test.size());

        // Nearest neighbor search - point returned close but not actually nearest neighbor
        test = new KdTree();
        points = new ArrayList<Point2D>();
        points.add(new Point2D(0.7, 0.2));
        points.add(new Point2D(0.5, 0.4));
        points.add(new Point2D(0.2, 0.3));
        points.add(new Point2D(0.4, 0.7));
        points.add(new Point2D(0.9, 0.6));
        for (Point2D point : points) {
            test.insert(point);
        }
        // (0.701, 0.907)
        test.nearest(new Point2D(0.701, 0.907));
    }

    // Unit Tests
    private void printTestResult(String testName, boolean result) {
        StdOut.println("TEST: " + testName + " RESULT: " + result);
    }
    private void RunNearestNeighborTests() {
        Point2D query;
        String testName;
        boolean result;

        StdOut.println("*****NOW PERFORMING NearestNeighborSearch() TESTS*****");

        testName = "Nearest neighbor search test 1: if query = point in set, should return equal point.";
        query = new Point2D(1, 1);
        NearestNeighborSearch nearestNeighborSearch = new NearestNeighborSearch(query, this);
        result = nearestNeighborSearch.nearestNeighbor.point.equals(query);
        this.printTestResult(testName, result);

        testName = "Nearest neighbor search test 2: query = (-1,2) -> NN should be (0,2)";
        query = new Point2D(-1, 2);
        nearestNeighborSearch = new NearestNeighborSearch(query, this);
        result = nearestNeighborSearch.nearestNeighbor.point.equals(new Point2D(0, 2));
        this.printTestResult(testName, result);

        testName = "Nearest neighbor search test 3: query = (1.5,0) -> NN should be (0,1)";
        query = new Point2D(-1, 2);
        nearestNeighborSearch = new NearestNeighborSearch(query, this);
        result = nearestNeighborSearch.nearestNeighbor.point.equals(new Point2D(0, 2));
        this.printTestResult(testName, result);
    }
    private void RunRangeSearchTests() {
        RectHV rect;
        String testName;
        boolean result;

        StdOut.println("*****NOW PERFORMING RANGESEARCH() TESTS*****");

        testName = "Range search test 1: should contain (0,1) and (0,2)";
        rect = new RectHV(-1, 0, 0.5, 3);
        RangeSearch rangeSearch = new RangeSearch(rect, this);
        result = rangeSearch.pointsInRange.contains(new Point2D(0, 1))
        && rangeSearch.pointsInRange.contains(new Point2D(0, 2))
        && rangeSearch.pointsInRange.size() == 2;
        this.printTestResult(testName, result);

        testName = "Range search test 2: should contain (1,0), (1,1), (2,0), and (2,1)";
        rect = new RectHV(0.5, -1, 2.5, 2.5);
        rangeSearch = new RangeSearch(rect, this);
        result = rangeSearch.pointsInRange.contains(new Point2D(1, 0))
        && rangeSearch.pointsInRange.contains(new Point2D(1, 1))
        && rangeSearch.pointsInRange.contains(new Point2D(2, 1))
        && rangeSearch.pointsInRange.size() == 3;
        this.printTestResult(testName, result);

    }
    private void RunContainsTests() {
        Point2D point;
        String testName;
        boolean result;

        StdOut.println("*****NOW PERFORMING CONTAINS() TESTS*****");

        testName = "KdTree contains test 1: contains all points inserted during Insertion Tests";
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        points.add(new Point2D(1, 1));
        points.add(new Point2D(1, 0));
        points.add(new Point2D(0, 1));
        points.add(new Point2D(2, 1));
        points.add(new Point2D(0, 2));
        points.add(new Point2D(0, -1));
        result = true;
        for (Point2D p : points) {
            if (!this.contains(p)) {
                result = false;
                StdOut.println("KdTree does not contain point (" + p.x() + ", " + p.y() + ")");
            }
        }
        this.printTestResult(testName, result);

        testName = "KdTree contains test 2: does not contain point (5, 5)";
        point = new Point2D(5, 5);
        result = !this.contains(point);
        this.printTestResult(testName, result);
    }
    private void RunInsertionTests() {
        String testName;
        boolean result;

        StdOut.println("*****NOW PERFORMING INSERT() TESTS*****");

        testName = "KdTree insert test 1: inserting first point (1,1)";
        Point2D point = new Point2D(1, 1);
        this.insert(point);
        result = this.size() == 1 
        && this.root.point.equals(point)
        && this.root.left == null
        && this.root.right == null
        && this.root.depth == 1
        && this.root.size == 1;
        this.printTestResult(testName, result);

        testName = "this insert test 2: (1, 0) is inserted to right of root";
        point = new Point2D(1, 0);
        this.insert(point);
        result = this.root.right.point.equals(point)
        && this.root.right.depth == 2
        && this.root.right.size == 1
        && this.size() == 2;
        this.printTestResult(testName, result);

        testName = "this insert test 3: (0, 1) is inserted to left of root";
        point = new Point2D(0, 1);
        this.insert(point);
        result = this.root.left.point.equals(point)
        && this.root.left.depth == 2
        && this.root.left.size == 1
        && this.size() == 3;
        this.printTestResult(testName, result);

        testName = "this insert test 4: (2, 1) is inserted to right of right of root";
        point = new Point2D(2, 1);
        this.insert(point);
        result = this.root.right.right.point.equals(point)
        && this.root.right.right.depth == 3
        && this.size() == 4;
        this.printTestResult(testName, result);

        testName = "this insert test 5: (0, 2) is inserted to right of left of root";
        point = new Point2D(0, 2);
        this.insert(point);
        result = this.root.left.right.point.equals(point)
        && this.root.left.right.depth == 3
        && this.size() == 5;
        this.printTestResult(testName, result);

        testName = "this insert test 6: (0, -1) is inserted to left of left of root";
        point = new Point2D(0, -1);
        this.insert(point);
        result = this.root.left.left.point.equals(point)
        && this.root.left.left.depth == 3
        && this.root.left.size == 3
        && this.size() == 6;
        this.printTestResult(testName, result);
    }
    // #endregion
}