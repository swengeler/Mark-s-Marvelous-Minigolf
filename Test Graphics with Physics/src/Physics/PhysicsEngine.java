package Physics;

// TEST

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;
import terrains.World;

public class PhysicsEngine {
	public static final Vector3f GRAVITY = new Vector3f(0, -30, 0);
	private List<Ball> balls;
	private World world;
	
	public PhysicsEngine(List<Ball> balls, World world){
		this.balls = balls;
		this.world = world;
	}
	
	public void tick(){
		for(Ball b:balls){
			b.move(world);
			b.checkGroundCollision(world);
			//b.checkMinSpeed(world);
		}
		
	}
}
