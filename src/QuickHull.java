// This is a java program to find a points in convex hull using quick hull method
// source: Alexander Hrishov's website
// http://www.ahristov.com/tutorial/geometry-games/convex-hull.html

import sim.util.Double2D;

import java.util.ArrayList;
import java.util.Scanner;

public class QuickHull {
    ArrayList<Double2D> quickHull(ArrayList<Double2D> points) {
        ArrayList<Double2D> convexHull = new ArrayList<>();
        if (points.size() < 3) {
            return points;
        }

        int minDouble2D = -1, maxDouble2D = -1;
        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).x < minX) {
                minX = points.get(i).x;
                minDouble2D = i;
            }
            if (points.get(i).x > maxX) {
                maxX = points.get(i).x;
                maxDouble2D = i;
            }
        }
        Double2D A = points.get(minDouble2D);
        Double2D B = points.get(maxDouble2D);
        convexHull.add(A);
        convexHull.add(B);
        points.remove(A);
        points.remove(B);

        ArrayList<Double2D> leftSet = new ArrayList<>();
        ArrayList<Double2D> rightSet = new ArrayList<>();

        for (Double2D p : points) {
            if (Double2DLocation(A, B, p) == -1)
                leftSet.add(p);
            else if (Double2DLocation(A, B, p) == 1)
                rightSet.add(p);
        }
        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }

    private double distance(Double2D A, Double2D B, Double2D C) {
        double ABx = B.x - A.x;
        double ABy = B.y - A.y;
        double num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0)
            num = -num;
        return num;
    }

    private void hullSet(Double2D A, Double2D B, ArrayList<Double2D> set,
                         ArrayList<Double2D> hull) {
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1) {
            Double2D p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        double dist = Integer.MIN_VALUE;
        int furthestDouble2D = -1;
        for (int i = 0; i < set.size(); i++) {
            Double2D p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestDouble2D = i;
            }
        }
        Double2D P = set.get(furthestDouble2D);
        set.remove(furthestDouble2D);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        ArrayList<Double2D> leftSetAP = new ArrayList<>();
        for (Double2D M : set) {
            if (Double2DLocation(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        // Determine who's to the left of PB
        ArrayList<Double2D> leftSetPB = new ArrayList<>();
        for (Double2D M : set) {
            if (Double2DLocation(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);

    }

    private int Double2DLocation(Double2D A, Double2D B, Double2D P) {
        double cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }

    public static void main(String args[]) {
        System.out.println("Quick Hull Test");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the number of points");
        int N = sc.nextInt();

        ArrayList<Double2D> points = new ArrayList<>();
        System.out.println("Enter the coordinates of each points: <x> <y>");
        for (int i = 0; i < N; i++) {
            double x = sc.nextInt();
            double y = sc.nextInt();
            Double2D e = new Double2D(x, y);
            points.add(i, e);
        }

        QuickHull qh = new QuickHull();
        ArrayList<Double2D> p = qh.quickHull(points);
        System.out
                .println("The points in the Convex hull using Quick Hull are: ");
        for (Double2D aP : p) {
            System.out.println("(" + aP.x + ", " + aP.y + ")");
        }
        sc.close();
    }
}
