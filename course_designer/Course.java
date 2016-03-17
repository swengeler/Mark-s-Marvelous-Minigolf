package course_designer;

import java.util.ArrayList;

public class Course {
	
	private final int X = 10;
	private final int Y = 10;
	private GameTile[][] grid = new GameTile[X][Y];
	
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	private Obstacle overLay = null;
	private boolean hasEndHole = false;
	private boolean hasStartPoint = false;

	public void addTile(int x, int y) {
		if (grid[x][y] == null) {
			grid[x][y] = new GameTile();
		}
		setEdges();
	}
	
	public void setEndHole(boolean hole) {
		hasEndHole = hole;
	}
	
	public boolean hasEndHole() {
		return hasEndHole;
	}
	
	public void setHole(int x, int y) {
		grid[x][y].setEnd(true);
	}
	
	public void removeHole(int x, int y) {
		grid[x][y].setEnd(false);
	}
	
	public boolean checkTileForHole(int x, int y) {
		if (grid[x][y].getEnd()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setStartPoint(boolean start) {
		hasStartPoint = start;
	}
	
	public boolean hasStartPoint() {
		return hasStartPoint;
	}
	
	public void setStart(int x, int y) {
		grid[x][y].setStart(true);
	}
	
	public void removeStart(int x, int y) {
		grid[x][y].setStart(false);
	}
	
	public boolean checkTileForStart(int x, int y) {
		if (grid[x][y].getStart()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setOverLay(Obstacle overLay) {
		this.overLay = overLay;
	}
	
	public Obstacle getOverLay() {
		return overLay;
	}
	
	public void resetCourse() {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				grid[x][y] = null;
			}
		}
		obstacles = new ArrayList<Obstacle>();
		hasStartPoint = false;
		hasEndHole = false;
	}
	
	public boolean checkTile(int x, int y) {
		if (grid[x][y] == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public void removeTile(int x, int y) {
		if (grid[x][y].getEnd()) {
			setEndHole(false);
		}
		if (grid[x][y].getStart()) {
			setStartPoint(false);
		}
		grid[x][y] = null;
		
		setEdges();
	}
	
	public void addObstacle(Obstacle obstacle) {
		
		obstacles.add(obstacle);
		
		/*
		Obstacle[] newAry = new Obstacle[obstacles.length + 1];
		for (int i = 0; i < obstacles.length; i++) {
			newAry[i] = obstacles[i];
		}
		newAry[obstacles.length] = obstacle;
		obstacles = newAry;
		*/
		
	}
	
	public void removeObstacle() {
		if (obstacles.size() > 0) {
		
		obstacles.remove(obstacles.size() - 1);	
		
		/*	Obstacle[] newAry = new Obstacle[obstacles.length - 1];
			for (int i = 0; i < newAry.length; i++) {
				newAry[i] = obstacles[i];
			}
		obstacles = newAry;
		*/
		}
		
	}
	
	public ArrayList<Obstacle> getObstacles() {
		return obstacles;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public void addEdge(Edge edge) {
		
		edges.add(edge);
		/*
		Obstacle[] newAry = new Obstacle[edges.length + 1];
		for (int i = 0; i < edges.length; i++) {
			newAry[i] = edges[i];
		}
		newAry[edges.length] = edge;
		edges = newAry;
		*/
		
	}
	
	public void resetEdges() {
		edges.clear();
	}
	
	public void setEdges() {
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].length; y++) {
				if (grid[x][y] != null) {
					
					if (x != 0 && grid[x - 1][y] != null) {
						grid[x][y].setLeftEdge(false);
					} else {
						grid[x][y].setLeftEdge(true);
					}
					
					if (x != grid.length - 1 && grid[x + 1][y] != null) {
						grid[x][y].setRightEdge(false);
					} else {
						grid[x][y].setRightEdge(true);
					}
					
					if (y != 0 && grid[x][y - 1] != null) {
						grid[x][y].setTopEdge(false);
					} else {
						grid[x][y].setTopEdge(true);
					}
					
					if (y != grid.length - 1 && grid[x][y + 1] != null) {
						grid[x][y].setBottomEdge(false);
					} else {
						grid[x][y].setBottomEdge(true);
					}
					
					if (x != 0 && y != 0 && grid[x - 1][y - 1] != null) {
						grid[x][y].setTopLeft(false);
					} else {
						grid[x][y].setTopLeft(true);
					}
					
					if (x != grid.length - 1 && y != 0 && grid[x + 1][y - 1] != null) {
						grid[x][y].setTopRight(false);
					} else {
						grid[x][y].setTopRight(true);
					}
					
					if (x != grid.length - 1 && y != grid[0].length - 1 && grid[x + 1][y + 1] != null) {
						grid[x][y].setBottomRight(false);
					} else {
						grid[x][y].setBottomRight(true);
					}
					
					if (x != 0 && y != grid[0].length - 1 && grid[x - 1][y + 1] != null) {
						grid[x][y].setBottomLeft(false);
					} else {
						grid[x][y].setBottomLeft(true);
					}
				}
			}
		}
	}
	
	public GameTile[][] getGrid() {
		return grid;
	}

}
