package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import graphics.GenericPanel;
import utils.Point3D;
import utils.Calculator;

import physicsengine.Obstacle;
import physicsengine.RoundObstacle;

public class Hole extends RoundObstacle {

	private final int radius = 11;
	private double centerX;
	private double centerY;
	private Ellipse2D.Double shape;
    private Color color;
    public static final double HOLE_CAPTURE_VEL = 0.5;

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
		if (Calculator.distancePointPoint(ball.getCenter(), super.getCenter()) <= ball.getRadius() + this.radius) {
			return true;
		}
		return false;
	}

	public boolean isBallIn(Ball ball) {
		if (Calculator.distancePointPoint(ball.getCenter(), super.getCenter()) < this.radius) {
			//System.out.println("Hole is hit");
			if(ball.getVelocity().getLength() < HOLE_CAPTURE_VEL) {
				//System.out.println("Ball is slow enough");
				return true;
			}
		}
		return false;
	}



}
