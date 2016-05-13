package entities;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import Physics.PhysicsEngine;

public class Ball extends Entity{
	public static final float RADIUS = 1.1925f;
	
	private static final float RUN_SPEED = 2;
	private static final float TURN_SPEED = 100;
	private static final float JUMP_POWER = 40;
	private static final float MIN_XVEL = 0;
	private static final float MIN_YVEL = 0;
	private static final float MIN_ZVEL = 0;
	private static final float COEFF_RESTITUTION = 0.56f;

	private static final float BR_DECIDER = (float) (Math.PI * 0.1);

	private Vector3f currentVel = new Vector3f();
	private Vector3f currentAcc = new Vector3f();
	private float currentTurnSpeed = 0;

	private boolean moving;
	private boolean bouncingEnabled = true;

	public Ball(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(World world){ // world really necessary?
		checkInputs(world);
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		Vector3f gravity = new Vector3f(PhysicsEngine.GRAVITY.x, PhysicsEngine.GRAVITY.y, PhysicsEngine.GRAVITY.z);
		gravity = (Vector3f) gravity.scale(DisplayManager.getFrameTimeSeconds());
		System.out.println("DisplayManager.getFrameTimeSeconds(): " + DisplayManager.getFrameTimeSeconds());

		Vector3f.add(currentVel, gravity, currentVel);

		Vector3f delta = new Vector3f(currentVel.x, currentVel.y, currentVel.z);
		delta.scale(DisplayManager.getFrameTimeSeconds());

		super.increasePosition(delta);
		System.out.println("Ball's position after moving: (" + getPosition().x  + "|" + getPosition().y + "|" + getPosition().z + ")");

		float terrainHeight = world.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
	}
	
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public boolean isMoving() {
		return moving;
	}

	private void jump(World world){
		if(!(super.getPosition().y > world.getHeightOfTerrain(this.getPosition().x, this.getPosition().z)))
			this.currentVel.y = JUMP_POWER;
	}

	private void checkInputs(World world){
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentVel.x += (float) (RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())));
			this.currentVel.z += (float) (RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())));

		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentVel.x += (float) -(RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())));
			this.currentVel.z += (float) -(RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())));
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if (Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else
			this.currentTurnSpeed = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			jump(world);
	}

	public void checkGroundCollision(World world) { // should be obsolete once PhysicsEngine is working properly

		// get list of possible "ground-tiles"
		// get list of possible faces of obstacles
		// test for collision for all of those faces -> get list of colliding faces
		// push ball back in direction of movement until none of those collide anymore
		// (check again whether new collision?)
		// check for closest face -> if multiple within d < 0.0001 or smth then resolve with multiple
		// resolve with closest face or "new face" from multiple

		float terrainHeight = world.getHeightOfTerrain(this.getPosition().x, this.getPosition().z);

		if (this.getPosition().y - Ball.RADIUS < terrainHeight) {
			System.out.println("Current velocity: ( " + currentVel.x + " | " + currentVel.y + " | " + currentVel.z + " )");
			System.out.println("Position before: ( " + getPosition().x + " | " + getPosition().y + " | " + getPosition().z + " )");
			super.getPosition().y = terrainHeight + Ball.RADIUS;
			System.out.println("Position after: ( " + getPosition().x + " | " + getPosition().y + " | " + getPosition().z + " )");
			Vector3f normal = world.getNormalOfTerrain(getPosition().x, getPosition().z);
			System.out.println("Normal: ( " + normal.x + " | " + normal.y + " | " + normal.z + " )");
			float angleme = (float) Math.acos((Vector3f.dot(normal, currentVel))/(normal.length() * currentVel.length()));
			float angle = (float)Math.PI - Vector3f.angle(normal, currentVel);
			System.out.println("Angle: " + angle + " Angleme: " + angleme);

			Vector3f newPartialVel = (Vector3f) normal.scale(2*Vector3f.dot(currentVel, normal));
			Vector3f.sub(newPartialVel, currentVel, currentVel);
			currentVel.negate();

			if (bouncingEnabled && (angle < (float)(Math.PI/2 - BR_DECIDER))) {
				System.out.println("Bouncing");
				// implement more complex mechanism for rolling/sliding behaviour on the ground
				currentVel.scale(COEFF_RESTITUTION);
			} else if (currentVel.length() > 0) {
				System.out.println("Rolling");
				Vector3f projectionOnPlane = new Vector3f();
				Vector3f projection = (Vector3f) normal.scale(Vector3f.dot(currentVel, normal)/normal.lengthSquared());
				System.out.println("Normal part: ( " + projection.x + " | " + projection.y + " | " + projection.z + " )");
				Vector3f.sub(currentVel, projection, projectionOnPlane);
				projection.set(projectionOnPlane.x, projectionOnPlane.y, projectionOnPlane.z);
				System.out.println("Projection: ( " + projectionOnPlane.x + " | " + projectionOnPlane.y + " | " + projectionOnPlane.z + " )");
				Vector3f frictionDir = (Vector3f) projectionOnPlane.scale(-1/projectionOnPlane.length()); // reverse the direction of movement and scale to be a unit vector
				System.out.println("Friction1: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				float angleSN = Vector3f.angle(new Vector3f(frictionDir.x,0,frictionDir.z), frictionDir);
				System.out.println("Gravity scaling: " + Math.cos(angleSN) + " Angle: " + angleSN);
				float frictionAcc = PhysicsEngine.COEFF_FRICTION * (PhysicsEngine.GRAVITY.length() * (DisplayManager.getFrameTimeSeconds()) * (float)(Math.cos(angleSN)));
				System.out.println("Friction accleration: " + frictionAcc);
				frictionDir = (Vector3f) frictionDir.scale(frictionAcc); // should now be the correctly scaled vector of the frictional ACCELERATION
				System.out.println("Friction2: ( " + frictionDir.x + " | " + frictionDir.y + " | " + frictionDir.z + " )");
				currentVel.set(projection.x, projection.y, projection.z);
				System.out.println("Velocity as projection: ( " + currentVel.x + " | " + currentVel.y + " | " + currentVel.z + " )");
				if (currentVel.length() > frictionDir.length())
					Vector3f.add(currentVel, frictionDir, currentVel);
				else
					currentVel.set(0,0,0);
				bouncingEnabled = false;
			}

			System.out.println("Velocity after: ( " + currentVel.x + " | " + currentVel.y + " | " + currentVel.z + " )\n");


		} else if (!bouncingEnabled) {
			bouncingEnabled = true;
		}

		/*

		if(this.getPosition().y < terrainHeight ){
			super.getPosition().y = terrainHeight;
			Vector3f normal = world.getNormalOfTerrain(getPosition().x, getPosition().z);
			Vector3f newPartialVel = (Vector3f) normal.scale(2*Vector3f.dot(currentVel, normal));
			Vector3f.sub(newPartialVel, currentVel, currentVel);
			currentVel.negate();
			currentVel.scale(COEFF_RESTITUTION);
		}
		*/
	}

	public void checkMinSpeed(World world){
		if(currentVel.x < MIN_XVEL)
			currentVel.x = 0;
		if(currentVel.y < MIN_YVEL && getPosition().y < world.getHeightOfTerrain(getPosition().x, getPosition().z)){
			currentVel.y = 0;
			getPosition().y = world.getHeightOfTerrain(getPosition().x, getPosition().z);
		}
		if(currentVel.z < MIN_ZVEL)
			currentVel.z = 0;
	}

	public float getRadius() {
		return 1.1925f;
	}

	public Vector3f getVelocity() {
		return currentVel;
	}
	
}