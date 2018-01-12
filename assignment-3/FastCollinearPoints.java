import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by CtheSky on 2016/9/9.
 */
public class FastCollinearPoints {
    private ArrayList<LineSegment> segmentList = new ArrayList<>();

    public FastCollinearPoints(Point[] points) {
            if (points == null) throw new IllegalArgumentException("points is null!");        
            for (Point p : points) {
                if (p == null) throw new java.lang.IllegalArgumentException("Null entry!");
            }
    
        // finds all line segments containing 4 or more points
        checkDuplicatePoints(points);        
        Point[] aux = Arrays.copyOf(points, points.length);
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            Arrays.sort(aux);
            Arrays.sort(aux, p.slopeOrder()); 
            
            int min = 1;
            int max = min;
            while (min < aux.length) {
                while (max < aux.length && p.slopeTo(aux[max]) == p.slopeTo(aux[min])) max++;
                if (max - min >= 3) {
                    Point pMin = aux[min].compareTo(p) < 0 ? aux[min] : p;
                    Point pMax = aux[max - 1].compareTo(p) > 0 ? aux[max - 1] : p;
                    if (p == pMin)
                        segmentList.add(new LineSegment(pMin, pMax));
                }
                min = max;
            }
        }     
    }

        public int numberOfSegments() {
        // the number of line segments
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