package course_designer;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public interface Obstacle {
	public void draw(Graphics2D g2d);
	public Point2D getReferencePoint();
    public boolean contains(double x, double y);
    public void translate(int x, int y);
} 
