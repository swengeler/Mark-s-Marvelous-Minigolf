package gameModes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.Timer;

import entities.Ball;
import entities.Course;
import entities.Edge;
import entities.GameTile;
import entities.Hole;
import graphics.GamePanel;
import graphics.GenericPanel;
import graphics.MasterGamePanel;
import main.MainFrame;
import physicsengine.PhysicsHandler;
import physicsengine.PolygonObstacle;
import physicsengine.RoundObstacle;
import utils.Point3D;
import utils.Vector;
import physicsengine.Obstacle;;

public class Game {

	private Ball ball1;
	private Ball ball2;
	private boolean multiplayer;
	private Course course;
	private PhysicsHandler ph;
	private Timer t;
	private MainFrame frame;
	private int score;
	public ArrayList<Obstacle> obstacles;
	private MasterGamePanel masterGamePanel;

	public Game(boolean multiplayer, Course course, MainFrame frame, MasterGamePanel masterGamePanel) {
		this.masterGamePanel = masterGamePanel;
		ball1 = new Ball();
		this.frame = frame;
		this.course = course;
		if(multiplayer)
			ball2 = new Ball();
		course.setBallToStart(ball1);
		ph = new PhysicsHandler(ball1,this);
		setObstacles();
		ph.addObstacles(obstacles);
		ph.setBallSpeed(new Vector(0,0,0));
		ph.start();
	}



	public PhysicsHandler getPh() {
		return ph;
	}


	public void addObstacles() {
        obstacles = new ArrayList<Obstacle>();

        int[] x1 = {200, 650, 650, 200};
        int[] y1 = {200, 200, 650, 650};
        Polygon p1 = new Polygon(x1, y1, 4);
        PolygonObstacle o1 = new PolygonObstacle(p1);
        //obstacles.add(o1);
        obstacles.addAll(o1.getCorners());
        //RoundObstacle o2 = new RoundObstacle(500, 500, 1);
        //obstacles.add(o2);

        RoundObstacle o3 = new RoundObstacle(200, 200, 50);
        obstacles.add(o3);

        ph.addObstacles(obstacles);
    }

	public Ball getBall1() {
		return ball1;
	}

	public Ball getBall2() {
		return ball2;
	}

	public boolean isMultiplayer() {
		return multiplayer;
	}

	public Course getCourse() {
		return course;
	}


	public void setObstacles() {
		obstacles = new ArrayList<Obstacle>();
		for(Obstacle o:course.getObstacles()){
			o.translate(-100, -20);
			//o.scale(140.0/50.0);
		}
		GameTile[][] grid = course.getGrid();
		int edgeSize = 10;
		for(int x=0; x<grid.length; x++)
			for(int y=0; y<grid[0].length; y++){

				if(course.checkTile(x, y)){
				if (grid[x][y].hasRightEdge()) {
					int[] xpoints = {(x * GenericPanel.TILE_SIZE) + (GenericPanel.TILE_SIZE - edgeSize), (x * GenericPanel.TILE_SIZE) + (GenericPanel.TILE_SIZE), (x * GenericPanel.TILE_SIZE) + (GenericPanel.TILE_SIZE), (x * GenericPanel.TILE_SIZE) + (GenericPanel.TILE_SIZE - edgeSize)};
					int[] ypoints = {(y * GenericPanel.TILE_SIZE), (y * GenericPanel.TILE_SIZE), ((y+1) * GenericPanel.TILE_SIZE), ((y+1) * GenericPanel.TILE_SIZE)};
					obstacles.add(new PolygonObstacle(new Polygon(xpoints,ypoints,4),true, Color.GRAY));

				}
				if (grid[x][y].hasLeftEdge()) {
					int[] xpoints = {(x * GenericPanel.TILE_SIZE), (x * GenericPanel.TILE_SIZE) + edgeSize, (x * GenericPanel.TILE_SIZE) + edgeSize, (x * GenericPanel.TILE_SIZE)};
					int[] ypoints = {(y * GenericPanel.TILE_SIZE), (y * GenericPanel.TILE_SIZE), ((y+1) * GenericPanel.TILE_SIZE), ((y+1) * GenericPanel.TILE_SIZE)};
					obstacles.add(new PolygonObstacle(new Polygon(xpoints,ypoints,4),true, Color.GRAY));

				}
				if (grid[x][y].hasTopEdge()) {
					int[] xpoints = {(x * GenericPanel.TILE_SIZE), ((x+1) * GenericPanel.TILE_SIZE), ((x+1) * GenericPanel.TILE_SIZE), x * GenericPanel.TILE_SIZE};
					int[] ypoints = {(y * GenericPanel.TILE_SIZE), (y * GenericPanel.TILE_SIZE), (y * GenericPanel.TILE_SIZE) + edgeSize, (y * GenericPanel.TILE_SIZE) + edgeSize};
					obstacles.add(new PolygonObstacle(new Polygon(xpoints,ypoints,4),true, Color.GRAY));
				}
				if (grid[x][y].hasBottomEdge()) {
					int[] xpoints = {(x * GenericPanel.TILE_SIZE), ((x+1) * GenericPanel.TILE_SIZE), ((x+1) * GenericPanel.TILE_SIZE), x * GenericPanel.TILE_SIZE};
					int[] ypoints = {((y+1) * GenericPanel.TILE_SIZE) - edgeSize, ((y+1) * GenericPanel.TILE_SIZE) - edgeSize, ((y+1) * GenericPanel.TILE_SIZE), ((y+1) * GenericPanel.TILE_SIZE)};
					obstacles.add(new PolygonObstacle(new Polygon(xpoints,ypoints,4),true, Color.GRAY));
				}
				if(grid[x][y].isEnd()){
					obstacles.add(new Hole((x+0.5)*GenericPanel.TILE_SIZE, (y+0.5)*GenericPanel.TILE_SIZE, false));
				}
			}
			}


	}

	public ArrayList<Obstacle> getObstacles(){
		return obstacles;
	}

	public void over(){
		ball1.multiplyVWith(0);
		course.setBallToStart(ball1);
		System.out.println("shot = "  + this.getScore());
		score=0;
	}

    public void addScore(){
    	this.score++;
    	masterGamePanel.UpdateScore(score);
    }
    public int getScore(){
    	return this.score;
    }



}
