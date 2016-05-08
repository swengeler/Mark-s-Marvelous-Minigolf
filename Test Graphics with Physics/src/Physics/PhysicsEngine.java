package Physics;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;
import entities.Entity;
import terrains.Terrain;
import terrains.World;

public class PhysicsEngine {
	public static final Vector3f GRAVITY = new Vector3f(0, -15f, 0);
	public static final float COEFF_GRAVITY = 9.813f;
	public static final float COEFF_RESTITUTION = 0.83f;
	public static final float COEFF_FRICTION = 0.15f;
	public static final float MASS_BALL = 0f;
	public static final float RADIUS_BALL = 0f;
	
	private List<Ball> balls;
	private List<Entity> entities;
	private List<Terrain> terrains;
	private World world;
	
	public PhysicsEngine(List<Ball> balls, World world){
		this.balls = balls;
		this.world = world;
		this.entities = world.getEntities();
	}
	
	public void addBall(Ball ball) {
		this.balls.add(ball);
	}
	
	public void tick(){
		for(Ball b:balls){
			//if (b.getVelocity() > 0) {
				b.move(world);
				b.checkGroundCollision(world);
				//b.checkMinSpeed(world);
				checkOrdinaryCollision(b);
			//}
		}
	}
	
	public void checkOrdinaryCollision(Ball b) { // might delegate this to own class CollisionSD (Static-Dynamic) and ball-ball collision to CollisionDD
		// based on position (also take into account height >> highest terrain point) of the ball, check whether collision occurs
		// height of terrain in radius distance around ball
		// possibly combine with object collision so that they aren't resolved separately if the ball hits both the ground and an obstacle
		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		// might consider putting these for-loops into the world class and letting it do all the work on its entities/terrains!!
		for (Entity e : entities) {
			collidingFaces.addAll(e.getCollidingFaces(b));
		}
		for (Terrain t : terrains) {
			// add faces as well, possibly based on location of the ball, since it takes too much time to get faces for the entire surface?
			// check whether ball is above highest point -> then no other check is necessary
			// construct faces of where the ball is actually position (x and y)
			// might actually get empty list here since ball might getCollidingFaces method already checks for actual collision
			// -> should also check for x/y position though, to narrow down results
			
			// collidingFaces.addAll(t.getCollidingFaces(b));
		}
		
		// moving the ball out of the obstacles/terrains that collision was detected with
		Vector3f revBallMovement = new Vector3f(b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
		revBallMovement.negate(revBallMovement.normalise(revBallMovement)).scale(0.0001f);
		while (b.collidesWith(collidingFaces)) {
			// move the ball back out
			//b.translate(revBallMovement);
			for (PhysicalFace f : collidingFaces) { // maybe overkill/takes more time than checking every time?
				if (!f.collidesWithBall(b))
					collidingFaces.remove(f);
			}
		}
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
