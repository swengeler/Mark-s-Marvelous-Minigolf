package Physics;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;

public class ShotData {
	
	private Vector3f endPosition;
	private ArrayList<Entity> obstaclesHit;

	public ShotData(Vector3f endPosition, ArrayList<Entity> obstaclesHit) {
		this.endPosition = new Vector3f(endPosition.x, endPosition.y, endPosition.z);
		this.obstaclesHit = obstaclesHit;
	}

	public Vector3f getEndPosition() {
		return endPosition;
	}

	public ArrayList<Entity> getObstaclesHit() {
		return obstaclesHit;
	}
	
}
