package course_designer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class TriangleObstacle implements Obstacle{
	private Point2D.Double coord;
	private Color color;
	private Polygon shape;
	private int length;
	private boolean overLay;
	
	public TriangleObstacle(int x, int y, int length, boolean overLay){
		this.length = length;
		coord = new Point2D.Double(x, y);
		int [] xCoordinates = {x, (int) (x-(0.5*length)), (int) (x+(0.5*length))};
		int [] yCoordinates = {y, y + length, y + length};
		shape = new Polygon(xCoordinates, yCoordinates,3);
		color = new Color(0, 0, 255);
		this.overLay = overLay;
	}

	public void draw(Graphics2D g2d) {
		if (overLay) {
			g2d.setColor(new Color(0, 0, 255, 75));
		} else {
			g2d.setColor(color);
		}
	    g2d.fill(shape);
	    g2d.setColor(Color.BLACK);
	    g2d.draw(shape);
	}

	public Point2D.Double getReferencePoint(){
		return coord;
	}
	public boolean contains(double x, double y) {
		return false;
	}

	@Override
	public void translate(int x, int y) {
		int [] xCoordinates = {x,(int) (x-(0.5*length)),(int) (x+(0.5*length))};
		int [] yCoordinates = {y,y+length,y+length};
		shape = new Polygon(xCoordinates, yCoordinates,3);
	}
}
