import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;

public class KdTree {

    private static final RectHV UNIT_SQUARE = new RectHV(0.0, 0.0, 1.0, 1.0);

    private static final boolean RED   = true;
    private static final boolean BLACK = false;

    private Node root;     // root of the BST
    private int size;      //  number of points in the tree.
    
    // KdTree helper node data type
    private static class Node {
        private Point2D key;  //The point.
        private boolean orientation = RED; //  orientation of the rectangle corresponding to this node.        
        private Node left, right;

        public Node(Point2D p, Node left, Node right, boolean orientation) {
            this.key = p;
            this.orientation = orientation;            
            this.left = left;
            this.right = right;
        }
    }
    
    public KdTree() {
        root = null;
        size = 0;
    }
    
    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return size;
    }    

    public void insert(Point2D p) {
        if (p==null) throw new java.lang.IllegalArgumentException("this is a null argumentk!");        

        root = insert(root, p, true);
    }

    private Node insert(Node h, Point2D p, boolean orientation) {
        if (h == null) {
            size++;
            return new Node(p, null, null, orientation);
        }

        // do not insert the point if it is already in the 2d-tree;
        // return the existing node instead.
        if (p.equals(h.key)) {
            return h;
        }

        // at the root we use the x-coordinate (if the point to be inserted has a smaller x-coordinate
        // than the point at the root, go left; otherwise go right);
        // then at the next level, we use the y-coordinate (if the point to be inserted has a smaller y-coordinate
        // than the point in the node, go left; otherwise go right);
        // then at the next level the x-coordinate, and so forth.
        if (compare(p, h)) {
            h.left = insert(h.left, p, !h.orientation);
        } else {
            h.right = insert(h.right, p, !h.orientation);
        }

        return h;
    }    
    
    private boolean compare(Point2D p, Node h) {
        int cmp = h.orientation ? Point2D.X_ORDER.compare(p, h.key) : Point2D.Y_ORDER.compare(p, h.key);
        return (cmp < 0);
    }    

    public boolean contains(Point2D p) {
        if (p==null) throw new java.lang.IllegalArgumentException("this is a null argumentk!");                
        Node node = root;
        while (node != null) {
            if (p.equals(node.key)) {
                return true;
            }

            node = compare(p, node) ? node.left : node.right;
        }

        return false;
    }    
    
    public void draw() {
        StdDraw.setScale(0.0, 1.0);
        draw(root, UNIT_SQUARE);
    }    

    private void draw(Node node, RectHV nodeRect) {
        if (node == null) {
            return;
        }

        // FAQ: How should I set the size and color of the points and rectangles when drawing?
        // http://coursera.cs.princeton.edu/algs4/checklists/kdtree.html
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        // draw the point
        node.key.draw();

        // draw the splitting lines
        Point2D fromPoint, toPoint;
        if (node.orientation) {
            StdDraw.setPenColor(StdDraw.RED);
            fromPoint = new Point2D(node.key.x(), nodeRect.ymin());
            toPoint = new Point2D(node.key.x(), nodeRect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            fromPoint = new Point2D(nodeRect.xmin(), node.key.y());
            toPoint = new Point2D(nodeRect.xmax(), node.key.y());
        }
        fromPoint.drawTo(toPoint);

        draw(node.left, leftNodeRect(node, nodeRect));
        draw(node.right, rightNodeRect(node, nodeRect));

    }    

    private RectHV leftNodeRect(Node node, RectHV nodeRect) {
        return node.orientation
                ? new RectHV(nodeRect.xmin(), nodeRect.ymin(), node.key.x(), nodeRect.ymax())
                : new RectHV(nodeRect.xmin(), nodeRect.ymin(), nodeRect.xmax(), node.key.y());
    }

    private RectHV rightNodeRect(Node node, RectHV nodeRect) {
        return node.orientation
                ? new RectHV(node.key.x(), nodeRect.ymin(), nodeRect.xmax(), nodeRect.ymax())
                : new RectHV(nodeRect.xmin(), node.key.y(), nodeRect.xmax(), nodeRect.ymax());
    }
    
    /**
     * All points in the 2d-tree that are inside the rectangle.
     *
     * @param rect the query rectangle.
     * @return all points in the set that are inside the rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect==null) throw new java.lang.IllegalArgumentException("this is a null argumentk!");                
        Queue<Point2D> pointsInRect = new Queue<Point2D>();
        range(root, UNIT_SQUARE, rect, pointsInRect);
        return pointsInRect;
    }   
    
    private void range(Node node, RectHV nodeRect, RectHV queryRect, Queue<Point2D> pointsInRect) {
        // To find all points contained in a given query rectangle, start at the root and recursively search for
        // points in both subtrees using the following pruning rule:
        // if the query rectangle does not intersect the rectangle corresponding to a node,
        // there is no need to explore that node (or its subtrees).
        // A subtree is searched only if it might contain a point contained in the query rectangle.
        if (node == null) {
            return;
        }

        if (queryRect.intersects(nodeRect)) {
            if (queryRect.contains(node.key)) {
                pointsInRect.enqueue(node.key);
            }

            range(node.left, leftNodeRect(node, nodeRect), queryRect, pointsInRect);
            range(node.right, rightNodeRect(node, nodeRect), queryRect, pointsInRect);
        }
    }    

    /**
     * A nearest neighbor in the 2d-tree to p; null if 2d-tree is empty.
     *
     * @param p the point to find a nearest neighbor to.
     * @return a nearest neighbor in the 2d-tree to p; null if 2d-tree is empty.
     */
    public Point2D nearest(Point2D p) {
        if (p==null) throw new java.lang.IllegalArgumentException("this is a null argumentk!");                
        return nearest(root, UNIT_SQUARE, p, null);
    }

    private Point2D nearest(Node node, RectHV nodeRect, Point2D queryPoint, Point2D nearestPoint) {
        // To find a closest point to a given query point, start at the root and recursively search in both subtrees
        // using the following pruning rule:
        // if the closest point discovered so far is closer than the distance between the query point and the rectangle
        // corresponding to a node, there is no need to explore that node (or its subtrees).
        // That is, a node is searched only if it might contain a point that is closer than the best one found so far.
        // The effectiveness of the pruning rule depends on quickly finding a nearby point.
        // To do this, organize your recursive method so that when there are two possible subtrees to go down,
        // you always choose the subtree that is on the same side of the splitting line as the query point as the first
        // subtree to explore  the closest point found while exploring the first subtree may enable pruning of the
        // second subtree.
        if (node == null) {
            return nearestPoint;
        }

        Point2D nearestPointCandidate = nearestPoint;
        double nearestDist = (nearestPointCandidate != null)
                ? queryPoint.distanceSquaredTo(nearestPointCandidate)
                : Double.MAX_VALUE;

        if (nearestDist > nodeRect.distanceSquaredTo(queryPoint)) {
            double dist = queryPoint.distanceSquaredTo(node.key);
            if (dist < nearestDist) {
                nearestPointCandidate = node.key;
            }

            RectHV leftNodeRect = leftNodeRect(node, nodeRect);
            RectHV rightNodeRect = rightNodeRect(node, nodeRect);

            if (compare(queryPoint, node)) {
                // explore left subtree first
                nearestPointCandidate = nearest(node.left, leftNodeRect, queryPoint, nearestPointCandidate);
                nearestPointCandidate = nearest(node.right, rightNodeRect, queryPoint, nearestPointCandidate);
            } else {
                // explore right subtree first
                nearestPointCandidate = nearest(node.right, rightNodeRect, queryPoint, nearestPointCandidate);
                nearestPointCandidate = nearest(node.left, leftNodeRect, queryPoint, nearestPointCandidate);
            }
        }

        return nearestPointCandidate;
    }
    
    public static void main(String[] args) {
        KdTree kdtree = new KdTree();        
        Point2D p = new Point2D(0.2, 0.3);
        RectHV rect = new RectHV(0.2, 0.2, 0.6, 0.6);
        kdtree.insert(p);
        for (int i = 0; i < 100; i++)
            kdtree.insert(new Point2D(StdRandom.uniform(), StdRandom.uniform()));
        rect.draw();
        StdDraw.circle(p.x(), p.y(), p.distanceTo(kdtree.nearest(p)));
        kdtree.draw();
        StdDraw.show(0);
        StdOut.println("Nearest to " + p.toString() + " = " +  kdtree.nearest(p));
        for (Point2D point : kdtree.range(rect))
            StdOut.println("In Range: " + point.toString());
    }        
}
    
    