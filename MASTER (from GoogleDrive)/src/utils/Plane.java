package utils;

public class Plane {

    private Vector v1;
    private Vector v2;
    private Point3D p;
    private Vector normalVector;

    public Plane(Vector v1, Vector v2, Point3D p) {
        this.v1 = v1.clone();
        this.v2 = v2.clone();
        this.p = p.clone();
        normalVector = Calculator.crossProduct(v1, v2);
    }

    public Vector getNormalVector() {
        return normalVector;
    }

    public Vector getV1() {
        return v1;
    }

    public Vector getV2() {
        return v2;
    }

    public Point3D getP() {
        return p;
    }

}
