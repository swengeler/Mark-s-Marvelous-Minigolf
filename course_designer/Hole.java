package course_designer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Hole extends RoundObstacle implements Obstacle {
	
	private final int radius = 10;
	private Ellipse2D.Double shape;
    private Color color;
	
	public Hole(double centerX, double centerY, boolean overLay){
		super(centerX, centerY, 0, overLay);
        shape = new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        color = Color.BLACK;
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
        g2d.fill(shape);
        g2d.setColor(Color.BLACK);
        g2d.draw(shape);
	}

	@Override
	public Point2D getReferencePoint() {
		// TODO Auto-generated method stub
		return null;
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
	

}
