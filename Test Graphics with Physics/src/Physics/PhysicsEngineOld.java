package Physics;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import entities.Ball;
import entities.Entity;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;

public class PhysicsEngineOld {

	private static final float NORMAL_TH = 0.001f;

	public static final float[] COEFFS_RESTITUTION = {0.67f, 0.85f};

	public static final Vector3f GRAVITY = new Vector3f(0, -230f, 0);
	public static final float COEFF_GRAVITY = 9.813f;
	public static final float COEFF_RESTITUTION = 0.75f;
	public static final float COEFF_FRICTION = 0.15f;

	private List<Ball> balls;
	private World world;

	public PhysicsEngineOld(List<Ball> balls, World world){
		this.balls = balls;
		this.world = world;
	}

	public void addBall(Ball ball) {
		this.balls.add(ball);
	}

	public void tick(){
		for (Ball b : balls){
			// apply accelerations and stuff like that first, to make sure that a ball can move due to gusts of wind or similar
			//b.applyAccelerations();
			b.checkInputs();
			if (b.isMoving()) {
				b.move();
				resolveBallCollision(b);
				resolveOrdinaryCollision(b);
			}
		}
	}

	public void resolveOrdinaryCollision(Ball b) { // might delegate this to own class CollisionSD (Static-Dynamic) and ball-ball collision to CollisionDD
		System.out.printf("Ball's velocity at start of check: (%f|%f|%f)\n", b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		ArrayList<PhysicalFace> useForCollision = new ArrayList<PhysicalFace>();
		collidingFaces.addAll(world.getCollidingFacesEntities(b));
		collidingFaces.addAll(world.getCollidingFacesTerrains(b));

		if (collidingFaces.isEmpty()) {
			System.out.println();
			return;
		}

		ArrayList<PhysicalFace> combined = new ArrayList<PhysicalFace>();
		combined.add(collidingFaces.get(0));
		System.out.println("Normal added: (" + combined.get(0).getNormal().x + "|" + combined.get(0).getNormal().y + "|" + combined.get(0).getNormal().z + ")");
		for (PhysicalFace f : collidingFaces) {
			boolean found = false;
			for (int i = 0; !found && i < combined.size(); i++) {
				if (Math.abs(Math.abs(f.getNormal().x) - Math.abs(combined.get(i).getNormal().x)) < NORMAL_TH &&
					Math.abs(Math.abs(f.getNormal().y) - Math.abs(combined.get(i).getNormal().y)) < NORMAL_TH &&
					Math.abs(Math.abs(f.getNormal().z) - Math.abs(combined.get(i).getNormal().z)) < NORMAL_TH)
					found = true;
			}
			if (!found) {
				System.out.println("Normal added: (" + f.getNormal().x + "|" + f.getNormal().y + "|" + f.getNormal().z + ")");
				combined.add(f);
			}
		}
		System.out.println("Number of planes after reduction: " + combined.size());
		collidingFaces = (ArrayList<PhysicalFace>) combined.clone();

		if (collidingFaces.size() > 1) {
			for (PhysicalFace f : combined) {
				float angle = Vector3f.angle(b.getVelocity(), f.getNormal());
				angle = (float) Math.min(angle, Math.PI - angle);
				System.out.println("Angle between vel and normal: " + Math.toDegrees(angle));
				if (collidingFaces.size() != 1 && Math.abs(Math.PI/2 - angle) < Math.toRadians(15)) {
					System.out.println("Normal removed: (" + f.getNormal().x + "|" + f.getNormal().y + "|" + f.getNormal().z + ")");
					collidingFaces.remove(f);
				}
			}
		}
		/*
		// moving the ball out of the obstacles/terrains that collision was detected with
		System.out.println("Ball's position before pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");
		Vector3f normal = new Vector3f(collidingFaces.get(0).getNormal().x, collidingFaces.get(0).getNormal().y, collidingFaces.get(0).getNormal().z);
		Vector3f revBallMovement = (Vector3f) normal.scale(Vector3f.dot(b.getVelocity(), normal)/normal.lengthSquared());
		System.out.println("Projection (revBallMovement): ( " + revBallMovement.x + " | " + revBallMovement.y + " | " + revBallMovement.z + " )");
		revBallMovement.normalise();
		revBallMovement.negate();
		revBallMovement.scale(0.0001f);
		System.out.println("After scaling and shit: ( " + revBallMovement.x + " | " + revBallMovement.y + " | " + revBallMovement.z + " )");

		//Vector3f revBallMovement = new Vector3f(b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
		//revBallMovement.negate(revBallMovement.normalise(revBallMovement)).scale(0.0001f);
		System.out.println("Unscaled ball movement vector: (" + b.getVelocity().x + "|" + b.getVelocity().y + "|" + b.getVelocity().z + ")");
		System.out.println("Reverse ball movement vector: (" + revBallMovement.x + "|" + revBallMovement.y + "|" + revBallMovement.z + ")");

		//if (revBallMovement.y > 0) {
			while (b.collidesWith(collidingFaces)) {
				// move the ball back out
				b.increasePosition(revBallMovement);
			}
			System.out.println("Ball's position after pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");
	*/
			/* go back one step, so that there is at least on face the ball collides with
			revBallMovement.scale(-1f);
			while (!b.collidesWith(collidingFaces))
				b.increasePosition(revBallMovement);

			for (PhysicalFace f : collidingFaces) {
				if (f.collidesWithBall(b))
					useForCollision.add(f);
			}

			long intermediate2 = System.currentTimeMillis();
			System.out.println("Time to push ball out and get remaining faces: " + (intermediate2 - before) + " (there are " + useForCollision.size() + " faces)");
			*/ useForCollision = collidingFaces;
			bounceOrdinaryCollision(useForCollision, b);
		//}
	}

	private void bounceOrdinaryCollision(ArrayList<PhysicalFace> faces, Ball b) {
		if (b.getVelocity().lengthSquared() > 0.01) {
			System.out.println("COLLISION OCCURS (with " + faces.size() + " faces)");
			for (PhysicalFace f : faces) {
				System.out.println(	"Normal: (" + f.getNormal().x + "|" + f.getNormal().y + "|" + f.getNormal().z + ") " +
									"P1: (" + f.getP1().x + "|" + f.getP1().y + "|" + f.getP1().z + ") " +
									"P2: (" + f.getP2().x + "|" + f.getP2().y + "|" + f.getP2().z + ") " +
									"P3: (" + f.getP3().x + "|" + f.getP3().y + "|" + f.getP3().z + ")");
				System.out.printf("Angle between normal and vector of movement: %f \n", Math.toDegrees(Vector3f.angle(b.getVelocity(), f.getNormal())));
			}

			// the one true face of all faces, a face among faces
			PhysicalFace collidingFace = PhysicalFace.combineFaces(faces, b);
			
			// moving the ball out of the obstacles/terrains that collision was detected with
			System.out.println("Ball's position before pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");
			Vector3f normal1 = new Vector3f(collidingFace.getNormal().x, collidingFace.getNormal().y, collidingFace.getNormal().z);
			Vector3f revBallMovement = (Vector3f) normal1.scale(Vector3f.dot(b.getVelocity(), normal1)/normal1.lengthSquared());
			System.out.println("Projection (revBallMovement): ( " + revBallMovement.x + " | " + revBallMovement.y + " | " + revBallMovement.z + " )");
			revBallMovement.normalise();
			revBallMovement.negate();
			revBallMovement.scale(0.0001f);
			System.out.println("After scaling and shit: ( " + revBallMovement.x + " | " + revBallMovement.y + " | " + revBallMovement.z + " )");

			//Vector3f revBallMovement = new Vector3f(b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
			//revBallMovement.negate(revBallMovement.normalise(revBallMovement)).scale(0.0001f);
			System.out.println("Unscaled ball movement vector: (" + b.getVelocity().x + "|" + b.getVelocity().y + "|" + b.getVelocity().z + ")");
			System.out.println("Reverse ball movement vector: (" + revBallMovement.x + "|" + revBallMovement.y + "|" + revBallMovement.z + ")");

			//if (revBallMovement.y > 0) {
			while (collidingFace.collidesWithBall(b)) {
				// move the ball back out
				b.increasePosition(revBallMovement);
			}
			System.out.println("Ball's position after pushing it out: (" + b.getPosition().x + "|" + b.getPosition().y + "|" + b.getPosition().z + ")");

			System.out.println("Current velocity: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )");
			Vector3f normal = new Vector3f(collidingFace.getNormal().x, collidingFace.getNormal().y, collidingFace.getNormal().z); // THIS IS WHERE THE PROGRAM CRASHES
			Vector3f normalForCalculation = new Vector3f(normal.x, normal.y, normal.z);
			System.out.println("Normal: ( " + normal.x + " | " + normal.y + " | " + normal.z + " )");
			float alpha = (float) Math.acos((Vector3f.dot(normal, b.getVelocity()))/(normal.length() * b.getVelocity().length()));
			float angle = Math.min(alpha, (float)(Math.PI - alpha));
			//float angle = (float)Math.PI - Vector3f.angle(normal, b.getVelocity());
			System.out.println("Angle to normal: " + angle + " Angle to plane: " + (Math.PI/2 - angle));

			Vector3f newPartialVel = (Vector3f) normalForCalculation.scale(2 * Vector3f.dot(b.getVelocity(), normal) * (1/normal.lengthSquared()));
			Vector3f.sub(b.getVelocity(), newPartialVel, b.getVelocity());
			//b.getVelocity().negate();
			System.out.println("Normal after using in operations: ( " + normal.x + " | " + normal.y + " | " + normal.z + " )");

			if ((Math.PI/2 - angle) * b.getVelocity().length() > Math.toRadians(20) * 100) {
				System.out.println("Bouncing");
				// implement more complex mechanism for rolling/sliding behaviour on the ground
				b.getVelocity().scale(COEFF_RESTITUTION);
			} else if (b.getVelocity().length() > 0) {
				System.out.println("Rolling");
				Vector3f projectionOnPlane = new Vector3f();
				System.out.println("Normal length squared: " + normal.lengthSquared());
				Vector3f projection = (Vector3f) normal.scale(Vector3f.dot(b.getVelocity(), normal)/normal.lengthSquared());
				System.out.println("Normal part: ( " + projection.x + " | " + projection.y + " | " + projection.z + " )");
				Vector3f.sub(b.getVelocity(), projection, projectionOnPlane);
				projection.set(projectionOnPlane.x, projectionOnPlane.y, projectionOnPlane.z);
				System.out.println("Projection: ( " + projectionOnPlane.x + " | " + projectionOnPlane.y + " | " + projectionOnPlane.z + " )");
				Vector3f frictionDir = (Vector3f) projectionOnPlane.scale(-1/projectionOnPlane.length()); // reverse the direction of movement and scale to be a unit vector
				System.out.println("Friction1: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				float angleSN = Vector3f.angle(new Vector3f(frictionDir.x,0,frictionDir.z), frictionDir);
				System.out.println("Gravity scaling: " + Math.cos(angleSN) + " Angle: " + angleSN);
				float frictionAcc = PhysicsEngineOld.COEFF_FRICTION * (PhysicsEngineOld.GRAVITY.length() * DisplayManager.getFrameTimeSeconds() * (float)(Math.cos(angleSN)));
				System.out.println("Friction accleration: " + frictionAcc);
				frictionDir = (Vector3f) frictionDir.scale(frictionAcc); // should now be the correctly scaled vector of the frictional ACCELERATION
				System.out.println("Friction2: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				b.getVelocity().set(projection.x, projection.y, projection.z);
				System.out.println("Velocity as projection: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )");
				if (b.getVelocity().length() > frictionDir.length())
					b.setVelocity(b.getVelocity().x + frictionDir.x, b.getVelocity().y + frictionDir.y, b.getVelocity().z + frictionDir.z);
				else {
					b.setVelocity(0, 0, 0);
				}
			}
			System.out.println("Velocity after: ( " + b.getVelocity().x + " | " + b.getVelocity().y + " | " + b.getVelocity().z + " )\n");
		} else {
			b.setVelocity(0, 0, 0);
			b.setMoving(false);
		}
	}

	public void resolveBallCollision(Ball b1) {
		Vector3f dist = new Vector3f(0, 0, 0);
		for (Ball b2 : this.balls) {
			if (!b1.equals(b2)) { // nested if -> not good
				Vector3f normal = new Vector3f(b2.getPosition().x - b1.getPosition().x, b2.getPosition().y - b1.getPosition().y, b2.getPosition().z - b1.getPosition().z);
				Vector3f.sub(b1.getPosition(), b2.getPosition(), dist);
				System.out.println("Distance between balls: " + normal.length());
				if (normal.length() < (2 * Ball.RADIUS)) {
					Vector3f revM = new Vector3f(b1.getVelocity().x, b1.getVelocity().y,b1.getVelocity().z);
					revM.negate();
					revM.normalise();
					revM.scale(0.001f);
					while (normal.lengthSquared() < Math.pow(Ball.RADIUS, 2)) {
						b1.increasePosition(revM);
						normal.set(b2.getPosition().x - b1.getPosition().x, b2.getPosition().y - b1.getPosition().y, b2.getPosition().z - b1.getPosition().z);
					}
					System.out.printf("Movement b1 before: (%f|%f|%f)\n", b1.getVelocity().x, b1.getVelocity().y, b1.getVelocity().z);
					System.out.printf("Movement b2 before: (%f|%f|%f)\n", b2.getVelocity().x, b2.getVelocity().y, b2.getVelocity().z);
					//Vector3f normal = new Vector3f(b2.getPosition().x - b1.getPosition().x, b2.getPosition().y - b1.getPosition().y, b2.getPosition().z - b1.getPosition().z);
					System.out.printf("Normal between balls: (%f|%f|%f)\n", normal.x, normal.y, normal.z);
					//Vector3f pointInPlane = new Vector3f(b1.getPosition().x + normal.x/2, b1.getPosition().y + normal.y/2, b1.getPosition().z + normal.z/2);
					b1.getVelocity().scale(COEFF_RESTITUTION);
					float alpha = Vector3f.angle(b1.getVelocity(), normal);
					alpha = Math.min(alpha, (float) (Math.PI - alpha));
					float factorB2 = (float) (Math.cos(alpha) * b1.getVelocity().length());
					float factorB1 = (float) (Math.sin(alpha) * b1.getVelocity().length());
					System.out.println("alpha = " + alpha);
					System.out.println("factorB1 = " + factorB1);
					System.out.println("factorB2 = " + factorB2);
					// set the velocity of the second ball to one along the normal of the collision (its already in the right direction because of the way the normal is created (from b1 to b2)
					normal.normalise();
					Vector3f projection = new Vector3f(b1.getVelocity().x, b1.getVelocity().y, b1.getVelocity().z);
					Vector3f.sub(projection, (Vector3f) b1.getVelocity().scale(Vector3f.dot(normal, projection)), projection);
					normal.scale(factorB2);
					System.out.printf("Calculated movement b2: (%f|%f|%f)\n", normal.x, normal.y, normal.z);
					b2.setVelocity(normal);
					b2.setMoving(true);
					projection.normalise();
					projection.scale(factorB1);
					System.out.printf("Calculated movement b1: (%f|%f|%f)\n", projection.x, projection.y, projection.z);
					b1.setVelocity(projection);
					System.out.printf("Movement b1 after: (%f|%f|%f)\n", b1.getVelocity().x, b1.getVelocity().y, b1.getVelocity().z);
					System.out.printf("Movement b2 after: (%f|%f|%f)\n", b2.getVelocity().x, b2.getVelocity().y, b2.getVelocity().z);
				}
			}
		}
	}

}
