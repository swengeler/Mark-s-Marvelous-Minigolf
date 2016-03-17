package physicsengine;

import java.awt.*;
import java.awt.geom.*;

import entities.Hole;
import graphics.GenericPanel;
import utils.Calculator;
import utils.Point3D;

public class RoundObstacle implements Obstacle {

	private Point3D center;
    private Color color;
    private double radius;
    private boolean overlay;

    public RoundObstacle(Point3D center, double radius) {
        this.radius = radius;
        this.center = center;
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    public RoundObstacle(double centerX, double centerY, double radius, boolean overlay) {
        this.radius = radius;
        this.center = new Point3D(centerX, centerY, 0);
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        this.overlay = overlay;
    }
    
    public RoundObstacle(double centerX, double centerY, double radius) {
        this.radius = radius;
        this.center = new Point3D(centerX, centerY,0);
        color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    }

    
    public boolean isOverlay() {
		return overlay;
	}

	public void setOverlay(boolean overlay) {
		this.overlay = overlay;
	}

	public Point3D getCenter() {
        return center;
    }

    public void draw(Graphics2D g2d, boolean inGame) {
    	Ellipse2D.Double Nshape = new Ellipse2D.Double(center.getX()-radius, center.getY()-radius, 2*radius, 2*radius);
    	if(inGame){
    		Nshape = new Ellipse2D.Double(Nshape.x * GenericPanel.PX_SCALE, Nshape.y * GenericPanel.PX_SCALE, Nshape.height * GenericPanel.PX_SCALE, Nshape.width * GenericPanel.PX_SCALE);
    	}
    	
    	g2d.setColor(color);
        g2d.fill(Nshape);
        g2d.setColor(Color.BLACK);
        g2d.draw(Nshape);
    }

    public boolean contains(double x, double y) {
        if (Calculator.distancePointPoint(x, y, center.getX(), center.getY() + radius) <= radius)
            return true;
        else
            return false;
    }

	@Override
	public Point3D getReferencePoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void translate(int x, int y) {
		center.translate(x,y,0);
	}
	
	public void scale(double s){
		radius = radius*s;
	}
	
	public double getRadius(){
		return radius;
	}

}
