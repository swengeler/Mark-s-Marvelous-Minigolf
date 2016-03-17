package entities;

import java.awt.Polygon;

public class Edge {

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
}
