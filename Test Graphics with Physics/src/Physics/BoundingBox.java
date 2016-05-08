package Physics;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;

public class BoundingBox {

	private float minX, minY, minZ, maxX, maxY, maxZ;
	
	public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public boolean inBoundingBox(Ball b) {
		Vector3f p = b.getPosition();
		float r = PhysicsEngine.RADIUS_BALL;
		return 	(p.x - r < maxX && p.x + r > minX) &&
				(p.y - r < maxY && p.y + r > minY) &&
				(p.z - r < maxZ && p.z + r > minZ);
	}
	
}
