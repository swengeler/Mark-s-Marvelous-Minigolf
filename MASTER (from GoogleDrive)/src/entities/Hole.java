package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import graphics.GenericPanel;
import utils.Point3D;

import physicsengine.Obstacle;
import physicsengine.RoundObstacle;

public class Hole extends RoundObstacle {

	private final int radius = 20;
	private double centerX;
	private double centerY;
	private Ellipse2D.Double shape;
    private Color color;
    public static final double HOLE_NUMBER = 10;

	public Hole(double centerX, double centerY, boolean overLay){
		super(centerX, centerY, 0, overLay);
		this.centerX = centerX;
		this.centerY = centerY;
        shape = new Ellipse2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        color = Color.BLACK;
	}

	@Override
	public void draw(Graphics2D g2d, boolean inGame) {
		Ellipse2D.Double Nshape = (Ellipse2D.Double) this.shape.clone();
    	if(inGame){
    		Nshape = new Ellipse2D.Double(this.shape.x * GenericPanel.PX_SCALE, this.shape.y * GenericPanel.PX_SCALE, this.shape.height * GenericPanel.PX_SCALE, this.shape.width * GenericPanel.PX_SCALE);
    	}
    	g2d.setColor(color);
        g2d.fill(Nshape);
        g2d.setColor(Color.BLACK);
        g2d.draw(Nshape);
	}

	public boolean contains(Ball ball) {
		if(ball.getX() <= 230 && ball.getX() >= 190 && ball.getY() <= 90 && ball.getY() >= 50) {
			return true;
		}
		return false;
	}

	public boolean isBallIn(Ball ball) {
		//if (super.contains(ball.getX(), ball.getY())) {
		//System.out.println("Location of hole: " + centerX + "/" + centerY);
		//if(ball.getX() <= centerX + radius && ball.getX() >= centerX - radius && ball.getY() <= centerY + radius && ball.getY() >= centerY + radius) {
		if(ball.getX() <= 230 && ball.getX() >= 190 && ball.getY() <= 90 && ball.getY() >= 50) {
			System.out.println("Hole is hit");
			if(ball.getVelocity().getLength() < HOLE_NUMBER) {
				System.out.println("Ball is slow enough");
				return true;
			}
		}
		return false;
	}



}
