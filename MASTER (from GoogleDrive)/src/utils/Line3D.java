package utils;

import java.awt.geom.Line2D;
import java.awt.Graphics2D;
import java.awt.Color;

public class Line3D {

    private Point3D p1;
    private Point3D p2;

    public Line3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        p1 = new Point3D(x1, y1, z1);
        p2 = new Point3D(x2, y2, z2);
    }

    public Line3D(Point3D p1, Point3D p2) {
        this.p1 = p1.clone();
        this.p2 = p2.clone();
    }

    public Line3D(Vector v) {
        p1 = new Point3D(0, 0, 0);
        p2 = new Point3D(v.getX(), v.getY(), v.getZ());
    }

    public Point3D getP1() {
        return p1;
    }

    public Point3D getP2() {
        return p2;
    }

    public double getX1() {
        return p1.getX();
    }

    public double getX2() {
        return p2.getX();
    }

    public double getY1() {
        return p1.getY();
    }

    public double getY2() {
        return p2.getY();
    }

    public double getZ1() {
        return p1.getZ();
    }

    public double getZ2() {
        return p2.getZ();
    }

    public Vector getVector() {
        return new Vector(p1, p2);
    }

    public void setLine(double x1, double y1, double z1, double x2, double y2, double z2) {
        p1.setLocation(x1, y1, z1);
        p2.setLocation(x2, y2, z2);
    }

    public void setLine(Point3D p1, Point3D p2) {
        if (p1 != null)
            this.p1.setLocation(p1);
        if (p2 != null)
            this.p2.setLocation(p2);
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0));
        g2d.draw(new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY()));
    }

    public Line3D clone() {
        return new Line3D(p1, p2);
    }

}
