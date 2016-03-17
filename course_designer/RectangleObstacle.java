package course_designer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class RectangleObstacle implements Obstacle {
	private Polygon shape;
    private Color color;
    private Point2D.Double coord;
    private int length;
    private boolean outLine = false;

	public RectangleObstacle(int x, int y, int length, boolean outLine){
		this.length = length;
		coord = new Point2D.Double(x, y);
		int [] xCoord = {x, x, x + length, x + length};
		int [] yCoord ={y, y + length, y + length, y};
		shape = new Polygon(xCoord,yCoord,4);
		color = new Color(255, 0, 0);
		this.outLine = outLine;
	}
	
	public Point2D getReferencePoint(){
		return coord;
	}
	
	public void draw(Graphics2D g2d) {
		if (outLine) {
			g2d.setColor(new Color(255, 0, 0, 75));
		} else {
			g2d.setColor(color);
		}
	    g2d.fill(shape);
	    g2d.setColor(Color.BLACK);
	    g2d.draw(shape);
	}

	@Override
	public boolean contains(double x, double y) {
		return false;
	}

	@Override
	public void translate(int x, int y) {
		int [] xCoord = {x, x, x + length, x + length};
		int [] yCoord ={y, y + length, y, y + length};
		shape = new Polygon(xCoord, yCoord, 4);
	}
}
