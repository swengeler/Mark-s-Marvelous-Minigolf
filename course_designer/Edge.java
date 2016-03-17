package course_designer;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;

public class Edge implements Obstacle{

	private Polygon edge = new Polygon();
	
	public Edge(int x, int y, int width, int height) {
		edge.addPoint(x, y);
		edge.addPoint((x + width), y);
		edge.addPoint((x + width), (y + height));
		edge.addPoint(x, (y + height));	
	}
	
	public Polygon getShape() {
		return edge;
	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
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
