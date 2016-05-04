package Physics;

import java.util.ArrayList;

import entities.Ball;

public class CollisionData {
	
	private ArrayList<PhysicalFace> faces;
	private ArrayList<PhysicalFace> collisionList;

	public CollisionData(ArrayList<PhysicalFace> f) {
		this.faces = f;
	}
	
	public ArrayList<PhysicalFace> collidesBall(Ball b) {
		collisionList = new ArrayList<PhysicalFace>();
		for (PhysicalFace f : faces) {
			if (f.collidesBall(b))
				collisionList.add(f);
		}
		return collisionList;
	}
	
}
