package utils;

public class Point3D {
	private double x,y,z;

	public Point3D(){
		x = 0;
		y = 0;
		z = 0;
	}
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getDistance(Point3D p){
		return Math.sqrt(Math.pow((this.x-p.x),2) + Math.pow((this.y-p.y),2) + Math.pow((this.z-p.z),2));
	}
	
	public void translate(double dx, double dy, double dz){
		this.x += dx;
		this.y += dy;
		this.z += dz;
	}
	
	public void translate(Vector v){
		this.x += v.getX();
		this.y += v.getY();
		this.z += v.getZ();
	}
	
	public void setLocation(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	 public void setLocation(Point3D p) {
	        x = p.getX();
	        y = p.getY();
	        z = p.getZ();
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
	
	public Point3D clone() {
        return new Point3D(x, y, z);
    }
	
	public String toString(){
    	return "[" + x + "|" + y + "|" + z + "]";
    }
	

}
