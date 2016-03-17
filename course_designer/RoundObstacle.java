package course_designer;

import java.awt.*;
import java.awt.geom.*;

import javafx.geometry.Point3D;

public class RoundObstacle implements Obstacle {

    private Ellipse2D.Double shape;
    private Color color;
    private double radius;
    boolean overLay;

    public RoundObstacle(Point3D center, double radius) {
        this.radius = radius;
        shape = new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, 2 * radius, 2 * radius);
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    public RoundObstacle(double centerX, double centerY, double radius, boolean overLay) {
        this.radius = radius;
        shape = new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        color = new Color(237, 247, 37);
        this.overLay = overLay;
    }

    public Point3D getCenter() {
        return new Point3D(shape.getX() + radius, shape.getY() + radius, 0);
    }

    public void draw(Graphics2D g2d) {
    	if (overLay) {
    		g2d.setColor(new Color(237, 247, 37, 75));
    	} else {
    		g2d.setColor(color);
    	}
        g2d.fill(shape);
        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
    }

	@Override
	public boolean contains(double x, double y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void translate(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point2D getReferencePoint() {
		// TODO Auto-generated method stub
		return null;
	}
}
