package utils;

import java.awt.geom.*;

import entities.Ball;

import java.awt.*;

public class Calculator {

    private static boolean PRINT_INFO_1 = false;
    private static boolean PRINT_INFO_2 = false;

    public static double distanceLinePoint(Line3D line, Point3D point) {
        double distance = Math.abs(Calculator.crossProduct(new Vector(line.getP1(), point) , line.getVector()).getLength() / line.getVector().getLength());
        return distance;
    }

    public static double distancePlanePoint(Plane plane, Point3D point) {
        Vector pointToPIP = new Vector(plane.getP(), point);
        double cp = Calculator.dotProduct(pointToPIP, plane.getNormalVector());
        double distance = Math.abs(cp / plane.getNormalVector().getLength());
        return distance;
    }

    public static double distancePointPoint(Point3D p1, Point3D p2) {
        double distance = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2) + Math.pow(p1.getZ() - p2.getZ(), 2));
        return distance;
    }

    public static double distancePointPoint(double x1, double y1, double x2, double y2) {
        double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        return distance;
    }

    /**
    * @param p The surface of a polygon closets to the ball.
    * @param v The current velocity (has a direction as well as a size) of the ball.
    * @param b The ball colliding with an obstacle.
    */
    public static Vector reflectedVector(Plane p, Ball b) {
        Vector v = b.getVelocity();
        // the normal vectors of the two planes p and h (helper plane constructed from the velocity vector of the ball and the normal vector of the surface it collides with)
        Vector normalP = p.getNormalVector();
        Line3D normalLine = new Line3D(normalP);
        Point3D Q1 = new Point3D(normalLine.getX1() - v.getX(), normalLine.getY1() - v.getY(), normalLine.getZ1() - v.getZ());
        Plane h = new Plane(v, normalP, Q1);
        Vector normalH = h.getNormalVector();
        // calculating the vector from Q1 to Q2 using normalP and normalH (it has to be perpendicular to both)
        Vector vQ1Q2 = Calculator.crossProduct(normalP, normalH);
        vQ1Q2.makeUnitVector();

        double value = Math.acos(Calculator.dotProduct(normalP, v) / (normalP.getLength() * v.getLength()));
        double alpha;
        if (value < 180 - value)
            alpha = value;
        else
            alpha = 180 - value;
        double distance = Math.abs(Math.sin(alpha) * v.getLength());

        Point3D Q2;
        Point3D test1 = new Point3D(Q1.getX() + distance * vQ1Q2.getX(), Q1.getY() + distance * vQ1Q2.getY(), Q1.getZ() + distance * vQ1Q2.getZ());;
        Point3D test2 = new Point3D(Q1.getX() - distance * vQ1Q2.getX(), Q1.getY() - distance * vQ1Q2.getY(), Q1.getZ() - distance * vQ1Q2.getZ());;
        if (Calculator.distanceLinePoint(normalLine, test1) < Calculator.distanceLinePoint(normalLine, test2))
            Q2 = new Point3D(Q1.getX() + 2 * distance * vQ1Q2.getX(), Q1.getY() + 2 * distance * vQ1Q2.getY(), Q1.getZ() + 2 * distance * vQ1Q2.getZ());
        else
            Q2 = new Point3D(Q1.getX() - 2 * distance * vQ1Q2.getX(), Q1.getY() - 2 * distance * vQ1Q2.getY(), Q1.getZ() - 2 * distance * vQ1Q2.getZ());
        Vector reflected = new Vector(new Point3D(normalLine.getX1(), normalLine.getY1(), normalLine.getZ1()), Q2);

        if (PRINT_INFO_2) {
            System.out.println("Vector of movement: (" + v.getX() + "|" + v.getY() + "|" + v.getZ() + ")");
            System.out.println("Normal vector of plane: (" + normalP.getX() + "|" + normalP.getY() + "|" + normalP.getZ() + ")");
            System.out.println("Normal line: (" + normalLine.getX1() + "|" + normalLine.getY1() + "|" + normalLine.getZ1() + ") to (" + normalLine.getX2() + "|" + normalLine.getY2() + "|" + normalLine.getZ2() + ")");
            System.out.println("Point Q1: (" + Q1.getX() + "|" + Q1.getY() + "|" + Q1.getZ() + ")");
            System.out.println("Normal vector of helper plane: (" + normalH.getX() + "|" + normalH.getY() + "|" + normalH.getZ() + ")");
            System.out.println("Vector from Q1 to Q2 (unit vector): (" + vQ1Q2.getX() + "|" + vQ1Q2.getY() + "|" + vQ1Q2.getZ() + "), length = " + vQ1Q2.getLength());
            System.out.println("Dot product and length of the vectors: dotProduct = " + Calculator.dotProduct(normalP, v) + ", l normalp = " + normalP.getLength() + ", l v = " + v.getLength());
            System.out.println("Angle value/alpha: " + value + "/" + alpha);
            System.out.println("Distance normalLine and Q1: d = " + distance);
            System.out.println("Points - Test 1: (" + test1.getX() + "|" + test1.getY() + "|" + test1.getZ() + "), test 2: (" + test2.getX() + "|" + test2.getY() + "|" + test2.getZ() + ")");
            System.out.println("Distance - Test 1: d = " + Calculator.distanceLinePoint(normalLine, test1) + ", test 2: d = " + Calculator.distanceLinePoint(normalLine, test2));
            System.out.println("Point Q2: (" + Q2.getX() + "|" + Q2.getY() + "|" + Q2.getZ() + ")");
            System.out.println("New vector: (" + reflected.getX() + "|" + reflected.getY() + "|" + reflected.getZ() + "), l = " + reflected.getLength());
        }

        return reflected;
    }

    public static double length(Line3D line) {
        double length = Math.sqrt(Math.pow(line.getX1() - line.getX2(), 2) + Math.pow(line.getY1() - line.getY2(), 2) + Math.pow(line.getZ1() - line.getZ2(), 2));
        return length;
    }

    public static Vector crossProduct(Vector v1, Vector v2) {
        double x3 = v1.getY() * v2.getZ() - v1.getZ() * v2.getY();
        double y3 = v1.getZ() * v2.getX() - v1.getX() * v2.getZ();
        double z3 = v1.getX() * v2.getY() - v1.getY() * v2.getX();
        return new Vector(x3, y3, z3);
    }

    public static double dotProduct(Vector v1, Vector v2) {
        double dotProduct = v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
        return dotProduct;
    }

}
