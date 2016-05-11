package Physics;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;
import entities.Entity;
import renderEngine.DisplayManager;
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
		this.terrains = world.getTerrains();
	}

	public void addBall(Ball ball) {
		this.balls.add(ball);
	}

	public void tick(){
		for(Ball b:balls){
			//if (b.getVelocity() > 0) {
				b.move(world);
				//b.checkMinSpeed(world);
				resolveOrdinaryCollision(b);
				//b.checkGroundCollision(world);
			//}
		}
	}

	public void resolveOrdinaryCollision(Ball b) { // might delegate this to own class CollisionSD (Static-Dynamic) and ball-ball collision to CollisionDD
		long before = System.currentTimeMillis();
		// based on position (also take into account height >> highest terrain point) of the ball, check whether collision occurs
		// height of terrain in radius distance around ball
		// possibly combine with object collision so that they aren't resolved separately if the ball hits both the ground and an obstacle
		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		ArrayList<PhysicalFace> useForCollision = new ArrayList<PhysicalFace>();
		// might consider putting these for-loops into the world class and letting it do all the work on its entities/terrains!!
		for (Entity e : entities) {
			collidingFaces.addAll(e.getCollidingFaces(b));
		}
		collidingFaces.addAll(world.getCollidingFaces(b));
		/*for (Terrain t : terrains) {
		// add faces as well, possibly based on location of the ball, since it takes too much time to get faces for the entire surface?
		// check whether ball is above highest point -> then no other check is necessary (perhaps also do so with checking the height of the terrain?)
		// construct faces of where the ball is positioned (x and y)
		// might actually get empty list here since getCollidingFaces method already checks for actual collision
		// -> should also check for x/y position though, to narrow down results

		// collidingFaces.addAll(t.getCollidingFaces(b));
		}*/

		long intermediate1 = System.currentTimeMillis();
		System.out.println("Time to get faces with new method: " + (intermediate1 - before) + " (there are " + collidingFaces.size() + " faces)");

		if (collidingFaces.isEmpty()) {
			System.out.println();
			return;
		}

		// moving the ball out of the obstacles/terrains that collision was detected with
		System.out.println("Ball's position before pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");
		Vector3f revBallMovement = new Vector3f(b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
		revBallMovement.negate(revBallMovement.normalise(revBallMovement)).scale(0.000001f);
		System.out.println("Reverse ball movement vector: (" + revBallMovement.x + "|" + revBallMovement.y + "|" + revBallMovement.z + ")");
		while (b.collidesWith(collidingFaces)) {
			// move the ball back out
			b.increasePosition(revBallMovement);
			/*for (PhysicalFace f : collidingFaces) { // maybe overkill/takes more time than checking every time? - also, would in the end remove all faces, not a good idea
				if (!f.collidesWithBall(b))
					collidingFaces.remove(f);
			}*/
		}
		System.out.println("Ball's position after pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");

		// go back one step, so that there is at least on face the ball collides with
		revBallMovement.negate(revBallMovement);
		b.increasePosition(revBallMovement);

		for (PhysicalFace f : collidingFaces) {
			if (f.collidesWithBall(b))
				useForCollision.add(f);
		}

		long intermediate2 = System.currentTimeMillis();
		System.out.println("Time to push ball out and get remaining faces: " + (intermediate2 - before) + " (there are " + useForCollision.size() + " faces)");

		bounceOrdinaryCollision(useForCollision, b);
		// get closest face and resolve collision with that one
		// if two or more are within a certain range, say 0.001 or something, then resolve with both using contact points and their normals (same for three)
		long after = System.currentTimeMillis();
		System.out.println("Time to check collision with new method: " + (after - before) + "\n");
	}

	private void bounceOrdinaryCollision(ArrayList<PhysicalFace> faces, Ball b) {
		long before = System.currentTimeMillis();
		ArrayList<PhysicalFace> combined = new ArrayList<PhysicalFace>();
		combined.add(faces.get(0));
		for (PhysicalFace f : faces) {
			boolean found = false;
			for (int i = 0; !found && i < combined.size(); i++) {
				if (f.getNormal().x == combined.get(i).getNormal().x && f.getNormal().y == combined.get(i).getNormal().y && f.getNormal().z == combined.get(i).getNormal().z)
					found = true;
			}
			if (!found) {
				System.out.println("Normal added: (" + f.getNormal().x + "|" + f.getNormal().y + "|" + f.getNormal().z + ")");
				combined.add(f);
			}
		}
		System.out.println("Number of planes after reduction: " + combined.size());
		long after = System.currentTimeMillis();
		System.out.println("Time to reduce colliding planes: " + (after - before));
		if (combined.size() == 1) {
			// resolve with one plane
			System.out.println("Current velocity: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )");
			System.out.println("Position before: ( " + b.getPosition().x + " | " + b.getPosition().y + " | " + b.getPosition().z + " )");
			System.out.println("Position after: ( " + b.getPosition().x + " | " + b.getPosition().y + " | " + b.getPosition().z + " )");
			Vector3f normal = combined.get(0).getNormal();
			System.out.println("Normal: ( " + normal.x + " | " + normal.y + " | " + normal.z + " )");
			float angleme = (float) Math.acos((Vector3f.dot(normal, b.getVelocity()))/(normal.length() * b.getVelocity().length()));
			float angle = (float)Math.PI - Vector3f.angle(normal, b.getVelocity());
			System.out.println("Angle: " + angle + " Angleme: " + angleme);

			Vector3f newPartialVel = (Vector3f) normal.scale(2*Vector3f.dot(b.getVelocity(), normal));
			Vector3f.sub(newPartialVel, b.getVelocity(), b.getVelocity());
			b.getVelocity().negate();

			if ((angle > (float)(Math.PI/2 - (float) (Math.PI * 0.1)))) {
				System.out.println("Bouncing");
				// implement more complex mechanism for rolling/sliding behaviour on the ground
				b.getVelocity().scale(COEFF_RESTITUTION);
			} else if (b.getVelocity().length() > 0) {
				System.out.println("Rolling");
				Vector3f projectionOnPlane = new Vector3f();
				Vector3f projection = (Vector3f) normal.scale(Vector3f.dot(b.getVelocity(), normal)/normal.lengthSquared());
				System.out.println("Normal part: ( " + projection.x + " | " + projection.y + " | " + projection.z + " )");
				Vector3f.sub(b.getVelocity(), projection, projectionOnPlane);
				projection.set(projectionOnPlane.x, projectionOnPlane.y, projectionOnPlane.z);
				System.out.println("Projection: ( " + projectionOnPlane.x + " | " + projectionOnPlane.y + " | " + projectionOnPlane.z + " )");
				Vector3f frictionDir = (Vector3f) projectionOnPlane.scale(-1/projectionOnPlane.length()); // reverse the direction of movement and scale to be a unit vector
				System.out.println("Friction1: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				float angleSN = Vector3f.angle(new Vector3f(frictionDir.x,0,frictionDir.z), frictionDir);
				System.out.println("Gravity scaling: " + Math.cos(angleSN) + " Angle: " + angleSN);
				float frictionAcc = PhysicsEngine.COEFF_FRICTION * (PhysicsEngine.GRAVITY.length() * (20 * DisplayManager.getFrameTimeSeconds()) * (float)(Math.cos(angleSN)));
				System.out.println("Friction accleration: " + frictionAcc);
				frictionDir = (Vector3f) frictionDir.scale(frictionAcc); // should now be the correctly scaled vector of the frictional ACCELERATION
				System.out.println("Friction2: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				b.getVelocity().set(projection.x, projection.y, projection.z);
				System.out.println("Velocity as projection: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )");
				if (b.getVelocity().length() > frictionDir.length())
					Vector3f.add(b.getVelocity(), frictionDir, b.getVelocity());
				else
					b.getVelocity().set(0,0,0);
			}

			System.out.println("Velocity after: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )\n");
		} else if (combined.size() == 2) {
			// resolve with two planes by using their normals and
		}
	}

	public void checkBallCollision(Ball b1) {
		Vector3f dist = new Vector3f(0,0,0);
		for (Ball b2 : this.balls) {
			if (!b1.equals(b2)) { // nested if -> not good
				Vector3f.sub(b1.getPosition(), b2.getPosition(), dist);
				if (dist.length() < (2 * b1.getRadius())) {
					// do a back-flip
				}
			}
		}
	}

}
