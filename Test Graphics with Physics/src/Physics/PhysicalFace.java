package Physics;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;

public class PhysicalFace {
	
	private Vector3f normal, point1, point2, point3, dist, smallest, largest;

	public PhysicalFace(Vector3f normal, Vector3f point1, Vector3f point2, Vector3f point3) {
		this.normal = new Vector3f(normal.x, normal.y, normal.z);
		this.point1 = new Vector3f(point1.x, point1.y, point1.z);
		this.point2 = new Vector3f(point2.x, point2.y, point2.z);
		this.point3 = new Vector3f(point3.x, point3.y, point3.z);
		dist = new Vector3f(0,0,0);
		prepareBounds();
	}
	
	public boolean collidesWithBall(Ball b) {
		dist.set((point1.x - b.getPosition().x), (point1.y - b.getPosition().y), (point1.z - b.getPosition().z));
		double distance = Vector3f.dot(normal, dist)/normal.length();
		double r = b.getRadius();
		if (distance <= b.getRadius() &&
			((b.getPosition().x - r) <= largest.x && (b.getPosition().x + r) >= smallest.x) &&
			((b.getPosition().y - r) <= largest.y && (b.getPosition().y + r) >= smallest.y) &&
			((b.getPosition().z - r) <= largest.z && (b.getPosition().z + r) >= smallest.z))
			return true;
		return false;
	}
	
	private void prepareBounds() {
		float minX = Math.min(point1.x, Math.min(point2.x, point3.x));
		float minY = Math.min(point1.y, Math.min(point2.y, point3.y));
		float minZ = Math.min(point1.z, Math.min(point2.z, point3.z));
		float maxX = Math.max(point1.x, Math.max(point2.x, point3.x));
		float maxY = Math.max(point1.y, Math.max(point2.y, point3.y));
		float maxZ = Math.max(point1.z, Math.max(point2.z, point3.z));
		smallest = new Vector3f(minX, minY, minZ);
		largest = new Vector3f(maxX, maxY, maxZ);
	
	}
	
}
