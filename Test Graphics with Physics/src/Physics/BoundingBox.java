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
		float r = Ball.RADIUS;
		return 	(p.x - r <= maxX && p.x + r >= minX) &&
				(p.y - r <= maxY && p.y + r >= minY) &&
				(p.z - r <= maxZ && p.z + r >= minZ);
	}
	
	public void print() {
		System.out.printf(	"\n\n" +
							"     (%9.4f|%9.4f|%9.4f) _____________ (%9.4f|%9.4f|%9.4f)\n" +
				"                                    /|           /|\n" + 
				"                                   / |          / |\n" +
				"                                  /  |         /  |\n" +
							" (%9.4f|%9.4f|%9.4f) /____________/ (%9.4f|%9.4f|%9.4f)\n" +
				"                                 |   |        |   |\n" + 
							"     (%9.4f|%9.4f|%9.4f) |________|___| (%9.4f|%9.4f|%9.4f)\n" +
				"                                 |   /        |   /\n" +
				"                                 |  /         |  /                                          y| /x\n" +
				"                                 | /          | /                                            |/__\n" +
							" (%9.4f|%9.4f|%9.4f) |/___________|/ (%9.4f|%9.4f|%9.4f)               z" +
				"\n\n", 
				maxX, maxY, minZ, 
				maxX, maxY, maxZ, 
				minX, maxY, minZ, 
				minX, maxY, maxZ, 
				maxX, minY, minZ, 
				maxX, minY, maxZ, 
				minX, minY, minZ, 
				minX, minY, maxZ);

	}

}
