package entities;

import utils.Vector;
import physicsengine.Acceleration;
import physicsengine.Obstacle;
import physicsengine.PolygonObstacle;
import physicsengine.RoundObstacle;
import utils.Calculator;
import utils.Line3D;
import utils.Point3D;

public class Ball {

	private Point3D center;
	private double radius;
	public static final double ST_RADIUS = 4.5;

	public static final double BALL_MASS = 1;
	private Vector velocity = new Vector();

	public Ball(){
		this(new Point3D(30,30,0), ST_RADIUS);
	}

	public Ball(Point3D center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Point3D getCenter() {
		return center;
	}

	public void setCenter(Point3D center) {
		this.center = center;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public double getTranslatedX(double dx){
		return this.center.getX() + dx;
	}

	public double getTranslatedY(double dy){
		return this.center.getY() + dy;
	}

	public double getTranslatedZ(double dz){
		return this.center.getZ() + dz;
	}

	public void translate(double dx, double dy, double dz){
		center.translate(dx,dy,dz);
	}

	public void translate(Vector v){
		center.translate(v);
	}

    public double getX() {
        return center.getX();
    }

    public double getY() {
        return center.getY();
    }

    public double getZ() {
        return center.getZ();
    }

    public boolean collides(Obstacle o) {
    	if(o instanceof PolygonObstacle){
    		for (int angle = 0; angle < 360 ; angle += 5) {
    			if (o.contains(center.getX() + Math.sin(Math.toRadians(angle)) * radius, center.getY() + Math.cos(Math.toRadians(angle)) * radius)) {
    				return true;
    			}
    		}
        return false;
    	} else if (o instanceof RoundObstacle){
			if (o instanceof Hole) {
				Hole temp = (Hole) o;
				return temp.contains(this);
			} else {
	    		RoundObstacle obs = (RoundObstacle) o;
	    		return Calculator.distancePointPoint(getCenter(), obs.getCenter()) <= (radius+obs.getRadius());
			}
    	}

    	return false;
    }

    public Point3D pushBallOut(Obstacle o) {
        Vector reversedUnitV = velocity.getUnitVector();
        reversedUnitV.reverse();
        while (this.collides(o)) {
            this.translate(reversedUnitV);
        }

        double curDistance;
        double smallestDistance = Double.MAX_VALUE;
        Point3D closestPoint = null;
        if (o instanceof PolygonObstacle) {
            PolygonObstacle op = (PolygonObstacle) o;

            for (int angle = 0; angle < 360; angle += 5) {
                for (Line3D edge : op.getEdges()) {
                    curDistance = Calculator.distanceLinePoint(edge, new Point3D(center.getX() + Math.sin(angle) * radius, center.getY() + Math.cos(angle) * radius, 0));
                    if (curDistance < smallestDistance) {
                        smallestDistance = curDistance;
                        closestPoint = new Point3D(center.getX() + Math.sin(angle) * radius, center.getY() + Math.cos(angle) * radius, 0);
                    }
                }
            }
        }

        return closestPoint;
    }

    public void pushBallOutWall(int wall) {
        // x-wall
        Vector reversedUnitV = velocity.getUnitVector();
        reversedUnitV.reverse();
        if (wall == 0) {
            while (center.getY() - radius < 0)
                this.translate(reversedUnitV);
            while (center.getY() + radius > 750)
                this.translate(reversedUnitV);
        } else if (wall == 1) {
            while (center.getX() - radius < 0)
                this.translate(reversedUnitV);
            while (center.getX() + radius > 750)
                this.translate(reversedUnitV);
        }

    }

    public void move(){
    	this.translate(velocity);
    }

	public void applyAcceleration(Acceleration a) {
		velocity.add(a);

	}

	public void multiplyVWith(double factor) {
        velocity.multiplyWith(factor);
    }

}
