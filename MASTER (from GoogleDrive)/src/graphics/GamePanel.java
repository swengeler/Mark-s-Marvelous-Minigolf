package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import gameModes.Game;
import entities.Course;
import entities.Edge;
import entities.GameTile;
import entities.Hole;
import main.MainFrame;
import physicsengine.Obstacle;
import utils.Line3D;
import utils.Point3D;
import utils.Vector;

public class GamePanel extends GenericPanel{

	private Game game;
	private MasterGamePanel masterGamePanel;
	boolean inGame;
	private boolean multiplayer;
	private static Image ballImage;
	private Line3D aiming;
	private Point3D hitPoint;
	private Vector hitVector;
	private int score=0;

	public GamePanel(MasterGamePanel masterGamePanel){
		this.masterGamePanel = masterGamePanel;
		ballImage = Toolkit.getDefaultToolkit().createImage("res\\Textures\\disc.png");
		hitPoint = new Point3D(0, 0, 0);
		hitVector = new Vector(0, 0, 0);
		aiming = new Line3D(0, 0, 0, 0, 0, 0);

		addMouseListener(new MouseAdapter(){
			boolean clicked = false;
			int x,y;
			@Override
			public void mousePressed(MouseEvent e) {
				if(!clicked && game.getPh().isStopped()){
					clicked = true;
					x = e.getX();
					y = e.getY();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(clicked){
					double xVel = e.getX() - x;
					double yVel = e.getY() - y;
					game.getPh().setBallSpeed(new Vector(-xVel*0.01, -yVel*0.01, 0));
					clicked = false;
					aiming = null;
					game.addScore();
				}
			}

		});

		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				hitPoint.setLocation(e.getX(), e.getY(), 0);
				hitVector.setValues(game.getBall1().getX() - e.getX(), game.getBall1().getY() - e.getY(), 0.0);
				aiming.setLine(hitPoint, new Point3D(hitPoint.getX() + hitVector.getX() * 100000, hitPoint.getY() + hitVector.getY() * 100000, 0));
				//System.out.println("Mouse dragged, line is " + aiming + ", point 1 is " + aiming.getP1() + ", point 2 is " + aiming.getP2());
			}
			public void mouseMoved(MouseEvent e) {}
		});
	}

	public void setCourse(Course course) {
		for (Edge e : course.getEdges()) {
			System.out.println(e.getInfo());
		}
		game = new Game(multiplayer, course, frame, masterGamePanel);
	}

	public Image getBallImage(){
		return ballImage;
	}

	public int getScore(){
		return score;
	}

	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		if (game != null) {
			drawCourse(g);
			drawAiming(g2);
			drawBall(g);
		}
	}

	public void drawAiming(Graphics2D g2d){
		if (aiming != null) {
			g2d.setColor(Color.RED);
			Line2D.Double line = new Line2D.Double(aiming.getX1(), aiming.getY1(), aiming.getX2(), aiming.getY2());
			g2d.draw(line);
		}
	}

	private void drawBall(Graphics g) {
		Point3D p = game.getBall1().getCenter();
		int r = (int) game.getBall1().getRadius();
		g.drawImage(ballImage, (int)((p.getX()-r)*PX_SCALE), (int)((p.getY()-r)*PX_SCALE), (int)(r*2*PX_SCALE), (int)(r*2*PX_SCALE), this);
	}

	private void drawCourse(Graphics g) {
		GameTile[][] tiles = game.getCourse().getGrid();
		for(int x=0; x<tiles.length; x++)
			for(int y=0; y<tiles[0].length; y++){
				if(game.getCourse().checkTile(x, y)){
					g.setColor(tiles[x][y].getBaseColor());
					g.fillRect((int)(x*TILE_SIZE*PX_SCALE), (int)(y*TILE_SIZE*PX_SCALE), (int)(TILE_SIZE*PX_SCALE), (int)(TILE_SIZE*PX_SCALE));
				}
			}

		ArrayList<Obstacle> obstacles = game.getObstacles();
		for(Obstacle o:obstacles){
			o.draw((Graphics2D) g, true);
		}

	}


}
