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

	public int[] xpoints() {
		return edge.xpoints;
	}

	public int[] ypoints() {
		return edge.ypoints;
	}

	public boolean contains(double x, double y) {
		for (double i = x - 1; i <= x + 1; i += 1) {
			for (double j = y - 1; j <= y + 1; j += 1) {
				if (edge.contains(i, j)) {
					if (Course.PRINT_EDGE_DEBUG) {System.out.println(this.getInfo() + " contains " + i + "/" + j);}
					return true;
				}
			}
		}
		return false;
	}

	public void addPoint(int x, int y) {
		edge.addPoint(x, y);
	}

	public String getInfo() {
		String info = "Edge with " + edge.xpoints.length + " corners: ";
		for (int i = 0; i < edge.xpoints.length; i++) {
			//info = info + "(" + edge.xpoints[i] + "|" + edge.ypoints[i] + ") ";
		}
		return info;
	}

}
