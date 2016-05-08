package Physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;

public class CollisionData {

	private BoundingBox bbox;
	private ArrayList<PhysicalFace> faces;
	private ArrayList<PhysicalFace> collisionList;

	public CollisionData() {
		faces = new ArrayList<PhysicalFace>();
		collisionList = new ArrayList<PhysicalFace>();
	}

	public CollisionData(Vector3f[] tfVertices, int[] indeces, BoundingBox bbox) {
		faces = new ArrayList<PhysicalFace>(indeces.length/3);
		collisionList = new ArrayList<PhysicalFace>();
		for (int i = 0; i < indeces.length; i++) {
			// get three points belonging together
			//faces.add(new PhysicalFace())
		}
		this.bbox = bbox;
	}

	public void addFace(PhysicalFace face) {
		faces.add(face);
	}

	public void setBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		bbox = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public ArrayList<PhysicalFace> getCollidingFaces(Ball b) {
		collisionList.clear();
		for (PhysicalFace f : faces) {
			if (f.collidesWithBall(b))
				collisionList.add(f);
		}
		return collisionList;
	}

	public boolean inBounds(Ball b) {
		return bbox.inBoundingBox(b);
	}

}
