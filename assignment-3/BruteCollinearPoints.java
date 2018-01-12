import java.util.ArrayList;
import java.util.Arrays;


public class BruteCollinearPoints {
    private ArrayList<LineSegment> segmentList = new ArrayList<>();
    
    // finds all line segmentObjects containing 4 points
    public BruteCollinearPoints(Point[] points)  {
        if (points == null) throw new IllegalArgumentException("points is null!");        
        for (Point p : points) {
           if (p == null) throw new java.lang.IllegalArgumentException("Null entry!");
        }
        checkDuplicatePoints(points);
        Point[] aux = Arrays.copyOf(points, points.length);
        Arrays.sort(aux);
        int len = aux.length;

        for (int i = 0; i < len; i++)
            for (int j = i + 1; j < len; j++)
                for (int k = j + 1; k < len; k++)
                    for (int m = k + 1; m < len; m++) {
                        Point p1 = aux[i], p2 = aux[j], p3 = aux[k], p4 = aux[m];
                        if (p1.slopeTo(p2) == p1.slopeTo(p3) && p1.slopeTo(p2) == p1.slopeTo(p4))
                            segmentList.add(new LineSegment(p1, p4));
                    }        
        
    }

    public int numberOfSegments()    {
     return segmentList.size();
    }
    public LineSegment[] segments() {
        // the line segments
        LineSegment[] segments = new LineSegment[segmentList.size()];
        return segmentList.toArray(segments);
    }
    
    private void checkDuplicatePoints(Point[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("Duplicated entries in given points.");
                }
            }
        }
    }   
    
}




    