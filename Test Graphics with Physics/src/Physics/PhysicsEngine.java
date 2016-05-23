package Physics;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import engineTester.MainGameLoop;
import entities.RealBall;
import entities.VirtualBall;
import entities.Ball;
import entities.Entity;
import terrains.Terrain;
import terrains.World;
import toolbox.Maths;

public class PhysicsEngine {

	private static final float NORMAL_TH = 0.001f;
	private static final float ANGLE_TH  = 5f;
	private static final float C = 0.001f;
	public static final float MIN_MOV_REQ = 0.000f;

	public static final float REAL_GRAVITY = 9.813f;

	public static final Vector3f GRAVITY = new Vector3f(0, -230f, 0);
	public static final float COEFF_RESTITUTION = 0.5f;
	public static final float COEFF_FRICTION = 0.15f;

	private List<RealBall> balls;
	private World world;
	private boolean enabled;

	public PhysicsEngine(List<RealBall> balls, World world) {
		this.balls = balls;
		this.world = world;
		this.enabled = true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addBall(RealBall ball) {
		this.balls.add(ball);
	}

	public void tick() {
		for (RealBall b : balls) {
			b.checkInputs();
            b.applyAccelerations();
			if ((b.isMoving() && b.movedLastStep()) || MainGameLoop.getCounter() < 10) {
				b.updateAndMove();
				System.out.println("\n---- Collision detection starts ----\n");
				resolveTerrainCollision(b);
				resolveBallCollision(b);
				resolveObstacleCollision(b);
				System.out.println("\n---- Collision detection ends ----\n");
			} else {
				b.setVelocity(0, 0, 0);
			}
		}
	}

	public void resolveTerrainCollision(Ball b) {
		/*
		 * The collision detection and resolution works according to the following steps:
		 * 1. Collision is detected based on triangle mesh of the terrain (NOT JUST height below ball)
		 * 2. Ball is pushed upwards until it no longer collides (according to the ball/mesh criteria)
		 * 3. The then-closest plane/triangle is taken as the point of contact and all further calculations are using that one plane
		 * 4. The movement of the ball is adjusted based on angle of incidence, velocity, friction/restitution etc. with the selected plane
		 */

		// get all faces/triangles of the terrain mesh that the ball collides with
		//ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		//collidingFaces.addAll(world.getCollidingFacesTerrains(b));

		// e.g. if the ball is above the maximum height of the terrain, then there is no need to actually resolve any collision
		//if (collidingFaces.isEmpty())
			//return;

		// push the ball out of the terrain so it remains on the surface
		// since its a terrain (which comes with certain restrictions) the ball is simply pushed upwards to simplify matters
		//while (b.collidesWith(collidingFaces))
			//b.increasePosition(0, 0.01f, 0);

		if (world.getHeightOfTerrain(b.getPosition().x, b.getPosition().z) > b.getPosition().y - Ball.RADIUS) {
			b.setPosition(new Vector3f(b.getPosition().x, world.getHeightOfTerrain(b.getPosition().x, b.getPosition().z) + Ball.RADIUS, b.getPosition().z));

			// calculate the closest face/plane after the ball was pushed out, which is then used for collision resolution
			Terrain t = world.getTerrain(b.getPosition().x, b.getPosition().z);
			if (t == null) {
				setEnabled(false);
				return;
			}
			float terrainX = b.getPosition().x - t.getX();
			float terrainZ = b.getPosition().z - t.getZ();
			float gridSquareSize = Terrain.getSize() / (float) (t.getHeights().length - 1);
			int gridX = (int) Math.floor(terrainX / gridSquareSize);
			int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
			float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
			float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
			PhysicalFace forResolution = null;
			if (xCoord <= (1 - zCoord)) {
				Vector3f v1 = new Vector3f(), v2 = new Vector3f(), p1 = new Vector3f(), p2 = new Vector3f(), p3 = new Vector3f(), normal = new Vector3f();
				p1.set(0, t.getHeights()[gridX][gridZ], 0);
				p2.set(1, t.getHeights()[gridX + 1][gridZ], 0);
				p3.set(0, t.getHeights()[gridX][gridZ + 1], 1);

				Vector3f.sub(p2, p1, v1);
				Vector3f.sub(p3, p1, v2);
				Vector3f.cross(v1, v2, normal);

				forResolution = new PhysicalFace(normal, p1, p2, p3);
			} else {
				Vector3f v1 = new Vector3f(), v2 = new Vector3f(), p1 = new Vector3f(), p2 = new Vector3f(), p3 = new Vector3f(), normal = new Vector3f();
				p1.set(1, t.getHeights()[gridX + 1][gridZ], 0);
				p2.set(1, t.getHeights()[gridX + 1][gridZ + 1], 1);
				p3.set(0, t.getHeights()[gridX][gridZ + 1], 1);

				Vector3f.sub(p2, p1, v1);
				Vector3f.sub(p3, p1, v2);
				Vector3f.cross(v1, v2, normal);

				forResolution = new PhysicalFace(normal, p1, p2, p3);
			}

			resolvePlaneCollision(b, forResolution);
			
		}
	}

	public void resolveObstacleCollision(Ball b) {
		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		collidingFaces.addAll(world.getCollidingFacesEntities(b));

		if (collidingFaces.isEmpty()) {
			System.out.println();
			return;
		}

		System.out.println("OBSTACLE COLLISON DETECTED");

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
		
		if (collidingFaces.size() == 1) {
			System.out.println("OBSTACLE COLLISION WITH ONE PLANE");
			// the simplest case, where the ball collides with one plane/face and the collision resolution is very simple
			resolvePlaneCollision(b, collidingFaces.get(0));
		} else if (collidingFaces.size() == 2) {
			System.out.println("OBSTACLE COLLISION WITH TWO PLANES");
			//for (PhysicalFace f : combined) {
			//	float angle = Vector3f.angle(b.getVelocity(), f.getNormal());
			//	angle = (float) Math.min(angle, Math.PI - angle);
			//	if (angle < Math.toRadians(ANGLE_TH) /*|| (angle * b.getVelocity().lengthSquared() * C < 1 && angle < Math.toRadians(45))*/) {
			//		collidingFaces.remove(f);
			//	}
			//}
			
			System.out.println("Plane 1: " + collidingFaces.get(0));
			System.out.println("Plane 2: " + collidingFaces.get(1));
			
			Vector3f[] pointsOfIntersection = Maths.intersectionPoints(collidingFaces.get(0), collidingFaces.get(1), b);
			for (int i = 0; i < pointsOfIntersection.length; i++) {
				System.out.printf("Intersection point %d: (%f|%f|%f)\n", (i + 1), pointsOfIntersection[i].x, pointsOfIntersection[i].y, pointsOfIntersection[i].z); 
			}
			
			if (collidingFaces.size() == 0) {
				// in that case the ball's movement is almost parallel to both planes, thus it should just move parallel to them
				
			} else if (collidingFaces.size() == 1) {
				// in that case the ball moves parallel to one plane and therefore simply collides with the other one
				resolvePlaneCollision(b, collidingFaces.get(0));
			} else {
				
			}
			
		} else if (collidingFaces.size() == 3) {
			System.out.println("OBSTACLE COLLISION WITH THREE PLANES");
		} else {
			System.out.println("OBSTACLE COLLISION WITH MORE THAN THREE PLANES");
		}

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
	}

	/*public void resolveOrdinaryCollision(Ball b) { // might delegate this to own class CollisionSD (Static-Dynamic) and ball-ball collision to CollisionDD
		while (b.getPosition().y - Ball.RADIUS < world.getHeightOfTerrain(b.getPosition().x, b.getPosition().z))
			b.increasePosition(0, 0.001f, 0);

		//if (b.getPosition().y - Ball.RADIUS < world.getHeightOfTerrain(b.getPosition().x, b.getPosition().z))
			//b.getPosition().y = w

		System.out.printf("Ball's velocity at start of check: (%f|%f|%f)\n", b.getVelocity().x, b.getVelocity().y, b.getVelocity().z);
		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		ArrayList<PhysicalFace> useForCollision = new ArrayList<PhysicalFace>();
		collidingFaces.addAll(world.getCollidingFacesEntities(b));
		collidingFaces.addAll(world.getCollidingFacesTerrains(b));

		if (collidingFaces.isEmpty()) {
			System.out.println();
			return;
		} else if (true)
			return;

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
				if (f.collidesWithFace(b))
					useForCollision.add(f);
			}

			long intermediate2 = System.currentTimeMillis();
			System.out.println("Time to push ball out and get remaining faces: " + (intermediate2 - before) + " (there are " + useForCollision.size() + " faces)");
			 useForCollision = collidingFaces;
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
			while (collidingFace.collidesWithPlane(b)) {
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
				float frictionAcc = PhysicsEngine.COEFF_FRICTION * (PhysicsEngine.GRAVITY.length() * b.getTimeElapsed() * (float)(Math.cos(angleSN)));
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
	}*/

	public void resolveBallCollision(Ball b1) {
		for (RealBall b2 : this.balls) {
			if (!b1.equals(b2)) {
				// the normal is chose from b1 to b2 so that it will not only be parallel to the new vector of movement of b2 but will also point in the right direction
				Vector3f normal = new Vector3f(b2.getPosition().x - b1.getPosition().x, b2.getPosition().y - b1.getPosition().y, b2.getPosition().z - b1.getPosition().z);
				if (normal.lengthSquared() < Math.pow(2 * Ball.RADIUS, 2)) {
					System.out.println("BALL COLLISION OCCURS");

					// the moving ball (b1) is pushed out of the previously unmoving ball (b2)
					Vector3f revM = new Vector3f(b1.getVelocity().x, b1.getVelocity().y, b1.getVelocity().z);
					revM.negate();
					revM.normalise();
					revM.scale(0.001f);
					while (normal.lengthSquared() < Math.pow(2 * Ball.RADIUS, 2)) {
						b1.increasePosition(revM.x, revM.y, revM.z);
						normal.set(b2.getPosition().x - b1.getPosition().x, b2.getPosition().y - b1.getPosition().y, b2.getPosition().z - b1.getPosition().z);
					}

					// this coefficient of restitution should be adapted to belong to ball-ball collisions (should be higher than for ball-green collisions)
					b1.getVelocity().scale(COEFF_RESTITUTION);

					// using simple geometry the magnitudes of the balls' velocity's after collision are calculated (their directions are along normal and tangent of the collision)
					float alpha = Vector3f.angle(b1.getVelocity(), normal);
					alpha = Math.min(alpha, (float) (Math.PI - alpha));
					float factorB2 = (float) (Math.cos(alpha) * b1.getVelocity().length());
					float factorB1 = (float) (Math.sin(alpha) * b1.getVelocity().length());

					// a projection of the vector of movement of the moving ball (b1) on the collision plane is used to calculate the direction of its movement after the collision
					// set the velocity of the second ball to one along the normal of the collision, scale it to the appropriate magnitude and update the "moving" status of the ball b2
					normal.normalise();
					Vector3f projection = new Vector3f(b1.getVelocity().x, b1.getVelocity().y, b1.getVelocity().z);
					Vector3f.sub(projection, (Vector3f) normal.scale(Vector3f.dot(normal, projection)), projection);
					normal.normalise();
					normal.scale(factorB2);
                    // check if the ball b1 is real, so that it would actually influence the other ball's movement
                    if (!(b1 instanceof VirtualBall)) {
    					b2.setVelocity(normal);
    					b2.setMoving(true);
                    }

					// set the velocity of the already previously moving ball to its appropriate magnitude and update b1's velocity
					if (projection.lengthSquared() > 0) {
    					projection.normalise();
    					projection.scale(factorB1);
					}
					b1.setVelocity(projection);
				}
			}
		}
	}
	
	private void resolvePlaneCollision(Ball b, PhysicalFace forResolution) {
		// calculate the angle between the plane that is used for collision resolution and the velocity vector of the ball
		// since Vector3f.angle(x, y) can compute angles over 90 degrees, there is an additional check to make sure the angle is below 90 degrees
		float temp = Vector3f.angle(forResolution.getNormal(), b.getVelocity());
		float angle = (float) Math.min(Math.PI - temp, temp);
		angle = (float) (Math.PI/2 - angle);

		// some pre-processing whose results will be used in both the bouncing and the rolling case
		// the normal component of the velocity is the projection of said velocity on the normal vector of the plane
		// that normal component will then be subtracted from the original velocity to get the new one
		Vector3f normal = new Vector3f(forResolution.getNormal().x, forResolution.getNormal().y, forResolution.getNormal().z);
		Vector3f normalComponent = new Vector3f(normal.x, normal.y, normal.z);
		normalComponent.scale(2 * Vector3f.dot(b.getVelocity(), normal) * (1/normal.lengthSquared()));
		Vector3f.sub(b.getVelocity(), normalComponent, b.getVelocity());

		// 45 degrees and 5 degrees are estimated
		if (angle > Math.toRadians(45) || (angle * b.getVelocity().lengthSquared() * C > 1 && angle > Math.toRadians(ANGLE_TH))) {
			// the ball is bouncing and the velocity can simply remain as is, only the coefficient of restitution has to be applied
			System.out.println("BOUNCING");
			b.scaleVelocity(COEFF_RESTITUTION);
		} else {
			// the ball is rolling (or sliding but that is not implemented (yet)), therefore a projection on the plane instead of a reflection is used
			System.out.println("ROLLING");
			Vector3f projection = new Vector3f();
			normalComponent.scale(-0.5f);
			Vector3f.sub(b.getVelocity(), normalComponent, projection);

			// friction is applied in the opposite direction as the movement of the ball, the vector can therefore be constructed from the projection
			Vector3f frictionDir = new Vector3f(projection.x, projection.y, projection.z);
			frictionDir.normalise();

			// the angle of inclination of the plane in the direction of movement/friction in respect to the horizontal plane
			// knowing this angle, the magnitude of the frictional force and acceleration can be computed
			// since F = m * a <=> F/m = a <=> F/m * t = v the effect on the velocity is computed as done below (Ffriction = coeffFriction * Fnormal)
			float angleIncl = Vector3f.angle(new Vector3f(frictionDir.x,0,frictionDir.z), frictionDir);
			angleIncl = (float) Math.min(Math.PI - angleIncl, angleIncl);
			float frictionVelComponent = PhysicsEngine.COEFF_FRICTION * (PhysicsEngine.GRAVITY.length() * (float) (Math.cos(angleIncl))) * b.getTimeElapsed();
			frictionDir = (Vector3f) frictionDir.scale(-frictionVelComponent);

			// finally, the velocity of the ball is set to the projection (since the ball is not supposed to be bouncing)
			// then friction is applied (if the effect of friction is larger than the actual velocity, the ball just stops)
			b.setVelocity(projection.x, projection.y, projection.z);
			if (b.getVelocity().lengthSquared() > frictionDir.lengthSquared())
				b.increaseVelocity(frictionDir);
			else {
				b.setVelocity(0, 0, 0);
				//b.setMoving(false);
			}
		}
	}

	public ShotData performVirtualShot(RealBall b, Vector3f shotVel) {
		ArrayList<Entity> obstaclesHit = new ArrayList<Entity>();

		// the position and velocity of the virtual ball which is updated instead of a real ball
		VirtualBall ball = new VirtualBall(b, shotVel);
		System.out.printf("Initial position of the virtual ball: (%f|%f|%f)\n", ball.getPosition().x, ball.getPosition().y, ball.getPosition().z);
		int counter = 0;
		while (ball.isMoving() || counter < 10) {
			ball.applyAccelerations();
			if ((ball.isMoving() && ball.movedLastStep()) || counter < 10) {
				ball.updateAndMove();
				resolveTerrainCollision(ball);
				resolveBallCollision(ball);
				obstaclesHit.addAll(world.getCollidingEntities(ball));
				//resolveObstacleCollision(ball);
			} else {
				ball.setVelocity(0, 0, 0);
				ball.setMoving(false);
			}
			counter++;
		}
		
		return new ShotData(ball.getPosition(), obstaclesHit);
	}

}
