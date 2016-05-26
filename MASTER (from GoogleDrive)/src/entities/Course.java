package entities;

import java.awt.Point;
import java.util.ArrayList;

import physicsengine.Obstacle;
import utils.Point3D;

public class Course {

	public static final boolean PRINT_EDGE_DEBUG = false;

	private final int X = 10;
	private final int Y = 10;
	private int[] s_e_tile = new int[4];  //{xStart,yStart,xEnd,yEnd}
	private GameTile[][] grid = new GameTile[X][Y];
	private ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private Obstacle overLay = null;
	private boolean hasEndHole = false;
	private boolean hasStartPoint = false;

	public void addTile(int x, int y) {
		grid[x][y] = new GameTile();
		setEdges();
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
	}

	public boolean checkTile(int x, int y) {
		if (grid[x][y] == null) {
			return false;
		} else {
			return true;
		}
	}

	public void removeTile(int x, int y) {
		grid[x][y] = null;
		setEdges();
	}

	public void addObstacle(Obstacle obstacle) {
		System.out.println("Adding " + obstacle);
		obstacles.add(obstacle);
	}

	public void removeObstacle() {
		if(!obstacles.isEmpty())
			obstacles.remove(obstacles.size()-1);

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

	public void combineEdges() {
	    //ArrayList<Edge> edges = this.getEdges();
		if (PRINT_EDGE_DEBUG) {System.out.println("Size of edges arraylist before combining: " + edges.size());}
	    for (int i = 0; i < edges.size(); i++) {
	        for (int j = i + 1; j < edges.size(); j++) {
	            int counter = 0;
	            for (int l = 0; l < edges.get(j).xpoints().length; l++) {
	                if (edges.get(i).contains(edges.get(j).xpoints()[l], edges.get(j).ypoints()[l])) {
						//System.out.println(edges.get(i).getInfo() + " contains " + edges.get(j).xpoints()[l] + "/" + edges.get(j).ypoints()[l] + " of " + edges.get(j).getInfo());
	                    counter++;
	                }
	            }
	            if (counter == 2) {
					if (PRINT_EDGE_DEBUG) {
						System.out.println("Combining edges: ");
						System.out.println(edges.get(i).getInfo());
						System.out.println(edges.get(j).getInfo());
						System.out.print("Points added to first edge: ");
					}
	                for (int l = 0; l < edges.get(j).xpoints().length; l++) {
						if (PRINT_EDGE_DEBUG) {System.out.print(edges.get(j).xpoints()[l] + "/" + edges.get(j).ypoints()[l] + " ");}
	                    edges.get(i).addPoint(edges.get(j).xpoints()[l], edges.get(j).ypoints()[l]);
	                }
					if (PRINT_EDGE_DEBUG) {System.out.println("\n");}
	                edges.remove(edges.get(j));
	                j--;
	            }
	        }
	    }
		if (PRINT_EDGE_DEBUG) {System.out.println("Size of edges arraylist after combining: " + edges.size());}
	}

	public GameTile[][] getGrid() {
		return grid;
	}

	public void setBallToStart(Ball ball1) {
		for(int i=0; i<grid.length; i++)
			for(int j=0; j<grid[0].length; j++)
			  if(grid[i][j] != null)
				if(grid[i][j].isStart()){
					initBall(i,j,ball1);
					return;
				}
	}

	private void initBall(int i, int j, Ball ball1) {
		ball1.setCenter(new Point3D(i*140+70,j*140+70, 0));
	}

	public static Course createDefaultCourse(){
		Course course = new Course();
		course.addTile(0, 0);
		course.addTile(0, 1);
		course.addTile(0, 2);

		course.setStart(0, 0);
		course.setHole(0, 2);
		return course;

	}

	public boolean hasEndHole() {
		return hasEndHole;
	}

	public void setHole(int i, int j) {
		grid[i][j].setEnd(true);

	}

	public void setEndHole(boolean b) {
		hasEndHole = b;
	}

	public void removeHole(int x, int y) {
		grid[x][y].setEnd(false);
	}

	public boolean hasStartPoint() {
		return hasStartPoint;
	}

	public void setStart(int x, int y) {
		grid[x][y].setStart(true);
	}

	public void setStartPoint(boolean start) {
		hasStartPoint = start;
	}

	public void removeStart(int x, int y) {
		grid[x][y].setStart(false);
	}

	public boolean checkTileForStart(int x, int y) {
		if (grid[x][y].isStart()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkTileForHole(int x, int y) {
		if (grid[x][y].isEnd()) {
			return true;
		} else {
			return false;
		}
	}

}
