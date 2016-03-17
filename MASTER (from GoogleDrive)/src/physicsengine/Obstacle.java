package physicsengine;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import utils.Point3D;

public interface Obstacle {
	public void draw(Graphics2D g2d, boolean inGame);
	public Point3D getReferencePoint();
    public boolean contains(double x, double y);
    public void translate(int x, int y);
    public void scale(double s);
} 
