package physicsengine;

import utils.Vector;

public class Friction extends Acceleration {

    public static final double GRASS = 0.25;

    private double factor;

    public Friction(double initX, double initY, double initZ, double coefficient) {
        super(initX, initY, initZ);
        factor = coefficient * 9.81 * (1.0 / 1000.0);
        x *= factor;
        y *= factor;
        z *= factor;
    }

    public Friction(Vector v, double coefficient) {
        super(v.getX(), v.getY(), v.getZ());
        factor = coefficient * 9.81 * (1.0 / 1000.0);
        x *= factor;
        y *= factor;
        z *= factor;
    }

    public void update(Vector v) {
        x = - v.getX() * factor;
        y = - v.getY() * factor;
        z = - v.getZ() * factor;
    }

    public void update(double ax, double ay, double az) {
        x = - ax * factor;
        y = - ay * factor;
        z = - az * factor;
    }
    
    public double getFactor(){
    	return factor;
    }

}
