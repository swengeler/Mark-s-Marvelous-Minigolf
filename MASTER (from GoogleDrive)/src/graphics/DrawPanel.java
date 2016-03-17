package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import entities.Edge;
import entities.GameTile;
import entities.Hole;
import physicsengine.Obstacle;
import entities.StartPoint;
import entities.Course;

public class DrawPanel extends GenericPanel{

	private static final long serialVersionUID = 1L;
	Course course;
	private GameTile[][] grid; 
	private ArrayList<Obstacle> obstacles;
	
	public DrawPanel(Course course) {
		this.course = course;
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		grid = course.getGrid();
		super.paintComponent(g);
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				if (grid[x][y] == null) {
					g.setColor(Color.DARK_GRAY);
					g.fillRect(100 + (x * SIZE), 20 + (y * SIZE), SIZE, SIZE);
					
				} else {
					g.setColor(grid[x][y].getBaseColor());
					g.fillRect(100 + (x * SIZE), 20 + (y * SIZE), SIZE, SIZE);
					drawEdge(x, y, g);
					drawCorners(x, y, g);
				}
				g.setColor(Color.BLACK);
				g.drawRect(100 + (x * SIZE), 20 + (y * SIZE), SIZE, SIZE);
				if (grid[x][y] != null && grid[x][y].isEnd()) {
					Obstacle hole = new Hole(100 + (x * SIZE) + 25, 20 + (y * SIZE) + 25, false);
					Graphics2D g2d = (Graphics2D) g;
					hole.draw(g2d,false);
				}
				if (grid[x][y] != null && grid[x][y].isStart()) {
					StartPoint start = new StartPoint(100 + (x * SIZE) + 27, 20 + (y * SIZE) + 27);
					Graphics2D g2d = (Graphics2D) g;
					start.draw(g2d);
				}
			}
		}
		drawObstacles(g);
		drawOverLay(g);
	}
	
	public void drawEdge(int x, int y, Graphics g) {
		int edgeSize = 3;
		if (grid[x][y].hasRightEdge()) {
			Edge right = new Edge(100 + (x * SIZE) + (SIZE - edgeSize), 20 + (y * SIZE), edgeSize, SIZE);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(right.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(right.getShape());
		}
		if (grid[x][y].hasLeftEdge()) {
			Edge left = new Edge(100 + (x * SIZE), 20 + (y * SIZE), edgeSize, SIZE);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(left.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(left.getShape());
		}
		if (grid[x][y].hasTopEdge()) {
			Edge top = new Edge(100 + (x * SIZE), 20 + (y * SIZE), SIZE, edgeSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(top.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(top.getShape());
		}
		if (grid[x][y].hasBottomEdge()) {
			Edge bottom = new Edge(100 + (x * SIZE), 20 + (y * SIZE) + (SIZE - edgeSize), SIZE, edgeSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(bottom.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(bottom.getShape());
		}
	}
	
	public void drawCorners(int x, int y, Graphics g) {
		int cornerSize = 3;
		if (grid[x][y].hasTopLeft()) {
			Edge topLeft = new Edge(100 + (x * SIZE), 20 +(y * SIZE), cornerSize, cornerSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(topLeft.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(topLeft.getShape());
		}
		if (grid[x][y].hasTopRight()) {
			Edge topRight = new Edge(100 + (x * SIZE) + (SIZE - cornerSize), 20 + (y * SIZE), cornerSize, cornerSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(topRight.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(topRight.getShape());
		}
		if (grid[x][y].hasBottomLeft()) {
			Edge bottomLeft = new Edge(100 + (x * SIZE), 20 + (y * SIZE) + (SIZE - cornerSize), cornerSize, cornerSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(bottomLeft.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(bottomLeft.getShape());
		}
		if (grid[x][y].hasBottomRight()) {
			Edge bottomRight = new Edge(100 + (x * SIZE) + (SIZE - cornerSize), 20 + (y * SIZE) + (SIZE - cornerSize), cornerSize, cornerSize);
			g.setColor(grid[x][y].getEdgeColor());
			g.fillPolygon(bottomRight.getShape());
			g.setColor(Color.BLACK);
			g.drawPolygon(bottomRight.getShape());
		}
	}
	
	public void drawObstacles(Graphics g) {
		obstacles = course.getObstacles();
		Graphics2D g2 = (Graphics2D) g;
		for (Obstacle obs:obstacles) {
			obs.draw(g2,false);
		}
	}
	
	public void drawOverLay(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Obstacle overLay = course.getOverLay();
		if (overLay != null) {
			overLay.draw(g2d,false);
		}
	}
	
}
