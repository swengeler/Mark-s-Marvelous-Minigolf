package Physics;


import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;
import terrains.World;

public class PhysicsEngine {
	public static final Vector3f GRAVITY = new Vector3f(0, -15f, 0);
	public static final float COEFF_GRAVITY = 9.813f;
	public static final float COEFF_RESTITUTION = 0.83f;
	public static final float COEFF_FRICTION = 0.15f;
	public static final float MASS_BALL = 0f;
	public static final float RADIUS_BALL = 0f;
	
	private List<Ball> balls;
	private World world;
	
	public PhysicsEngine(List<Ball> balls, World world){
		this.balls = balls;
		this.world = world;
	}
	
	public void addBall(Ball ball) {
		this.balls.add(ball);
	}
	
	public void tick(){
		for(Ball b:balls){
			b.move(world);
			//b.checkObjectCollision() -> to be implemented
			b.checkGroundCollision(world);
			//b.checkMinSpeed(world);
		}
	}
	
	public void checkGroundCollision(Ball b) {
		// based on position (also take into account height >> highest terrain point) of the ball, check whether collision occurs
		// height of terrain in radius distance around ball
		// possibly combine with object collision so that they arent resolved separately if the ball hits both the ground and an obstacle
	}
	
	public void checkObstacleCollision(Ball b) {
		// also include collision with a ball!!!
	}
	
	public void checkBallCollision(Ball b1) {
		for (Ball b2:this.balls) {
			if (!b1.equals(b2)) {
				// do something
			}
		}
	}
	
}
