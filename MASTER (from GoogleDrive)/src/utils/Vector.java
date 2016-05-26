package utils;

public class Vector {
	protected double x,y,z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Point3D p1, Point3D p2) {
		this.x = p2.getX()-p1.getX();
		this.y = p2.getY()-p1.getY();
		this.z = p2.getZ()-p1.getZ();
	}

	public Vector(Line3D line){
		this.x = line.getX2()-line.getX1();
		this.y = line.getY2()-line.getY1();
		this.z = line.getZ2()-line.getZ1();
	}

	public Vector() {
		this(0,0,0);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setValues(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getLength() {
        double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return length;
    }

    public Line3D toLineWithBasePoint(double bpX, double bpY, double bpZ) {
        Line3D line = new Line3D(bpX - x, bpY - y, bpZ - z, bpX, bpY, bpZ);
        return line;
    }

    public void makeUnitVector() {
        double length = getLength();
        x = x / length;
        y = y / length;
        z = z / length;
    }

    public Vector getUnitVector() {
        Vector v = this.clone();
        v.makeUnitVector();
        return v;
    }

    public void reverse() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void multiplyWith(double factor) {
        x *= factor;
        y *= factor;
        z *= factor;
    }

    public double getSizeFactor(int timeInMillis) {
        timeInMillis /= 3;
        double factor = getLength() - 8000 * Math.pow(timeInMillis * 0.000001, 2);
        if (factor >= 0)
            return factor;
        else
            return 0;
    }

    public Vector clone() {
        return new Vector(x, y, z);
    }

    public void add(Vector v){
    	x += v.getX();
    	y += v.getY();
    	z += v.getZ();
    }

    public String toString(){
    	return "[" + x + "|" + y + "|" + z + "]";
    }


}
