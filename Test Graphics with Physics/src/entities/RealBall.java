package entities;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import Physics.PhysicalFace;
import Physics.PhysicsEngine;

public class RealBall extends Entity implements Ball {
	
	private static final float FACTOR = 1;
	private static final float RUN_SPEED = 2;
	private static final float TURN_SPEED = 100;
	private static final float JUMP_POWER = 40;

	public static final float REAL_RADIUS = 0.04267f;
	public static final float REAL_MASS = 0.04593f;
	public static final float RADIUS = 1f;

	private Vector3f currentVel;
	private Vector3f currentAcc;
    private Vector3f lastPosition;
	private ArrayList<Vector3f> accelerations;

	private float currentTurnSpeed;
	private float lastTimeElapsed;

	private boolean moving;
	private boolean enableControls;

	public RealBall(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, boolean enableControls) {
		super(model, position, rotX, rotY, rotZ, scale, "ball");
        this.currentVel = new Vector3f();
        this.currentAcc = new Vector3f();
        this.lastPosition  = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
        this.accelerations = new ArrayList<Vector3f>();
		this.moving = true;
		this.enableControls = enableControls;
		this.addAccel(PhysicsEngine.GRAVITY);
	}

	public void updateAndMove() {
		super.increaseRotation(0, currentTurnSpeed * getTimeElapsed(), 0);
		lastPosition.set(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		lastTimeElapsed = getTimeElapsed();

		// based on the newly updated velocity, move the ball
		Vector3f delta = new Vector3f(currentVel.x, currentVel.y, currentVel.z);
		delta.scale(getTimeElapsed());
		super.increasePosition(delta);

		System.out.printf("Ball's position after moving: (%f|%f|%f)\n", getPosition().x, getPosition().y, getPosition().z);
		System.out.printf("Ball's velocity after moving (with gravity applied): (%f|%f|%f)\n", currentVel.x, currentVel.y, currentVel.z);
	}

	public void addAccel(Vector3f accel) {
		accelerations.add(accel);
	}

	public void removeAccel(Vector3f accel) {
		accelerations.remove(accel);
	}

    public void applyAccelerations() {
        for (Vector3f a : accelerations) {
			System.out.printf("Acceleration applied: (%f|%f|%f)\n", a.x, a.y, a.z);
			currentAcc.set(a.x, a.y, a.z);
			currentAcc.scale(getTimeElapsed());
			Vector3f.add(currentVel, currentAcc, currentVel);
			System.out.printf("... and velocity after: (%f|%f|%f)\n", currentVel.x, currentVel.y, currentVel.z);
		}
    }

	public void resetLastPos() {
		lastPosition.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	}

	public void setMoving(boolean moving) {
		System.out.println("Moving set to " + moving);
		this.moving = moving;
		if (moving)
			resetLastPos();
	}

	public boolean isMoving() {
		return moving;
	}

	private void jump() {
		setMoving(true);
		this.currentVel.y = JUMP_POWER;
		System.out.println("Moving set to " + moving + " (velocity now: (" + currentVel.x + "|" + currentVel.y + "|" + currentVel.z + ")");
	}

	public void checkInputs() {
		if (enableControls) {
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				this.currentVel.x += (float) (RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())))/FACTOR;
				this.currentVel.z += (float) (RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())))/FACTOR;
				setMoving(true);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				this.currentVel.x += (float) -(RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())))/FACTOR;
				this.currentVel.z += (float) -(RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())))/FACTOR;
				setMoving(true);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				this.currentVel.z += 40;
				System.out.println("x-speed increased by pressing up-arrow");
				setMoving(true);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				this.currentVel.z -= 40;
				System.out.println("x-speed decreased by pressing down-arrow");
				setMoving(true);
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				this.currentTurnSpeed = -TURN_SPEED;
				setMoving(true);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				this.currentTurnSpeed = TURN_SPEED;
				setMoving(true);
			} else
				this.currentTurnSpeed = 0;

			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				jump();
		}
	}

	public boolean collidesWith(ArrayList<PhysicalFace> faces) {
		for (PhysicalFace f : faces) {
			if (f.collidesWithFace(this))
				return true;
		}
		return false;
	}

	public void checkMinSpeed() {

	}

	public Vector3f getVelocity() {
		return currentVel;
	}

	public void setVelocity(Vector3f v) {
        System.out.printf("Velocity of ball %s set to: (%f|%f|%f)\n", this.toString(), v.x, v.y, v.z);
		currentVel.set(v.x, v.y, v.z);
	}

	public void scaleVelocity(float s) {
		currentVel.scale(s);
	}

	public void increaseVelocity(float x, float y, float z) {
		currentVel.x += x;
		currentVel.y += y;
		currentVel.z += z;
	}

	public void increaseVelocity(Vector3f v) {
		currentVel.x += v.x;
		currentVel.y += v.y;
		currentVel.z += v.z;
	}

	public void setVelocity(float x, float y, float z) {
		System.out.printf("Velocity of ball %s set to: (%f|%f|%f)\n", this.toString(), x, y, z);
		currentVel.set(x, y, z);
	}

	public boolean movedLastStep() {
		System.out.printf("Difference in positions: (%f|%f|%f)\n", super.getPosition().x - lastPosition.x, super.getPosition().y - lastPosition.y, super.getPosition().z - lastPosition.z);
		boolean moved = (Math.pow(super.getPosition().x - lastPosition.x, 2) +
						Math.pow(super.getPosition().y - lastPosition.y, 2) +
						Math.pow(super.getPosition().z - lastPosition.z, 2) >
						Math.pow(PhysicsEngine.MIN_MOV_REQ, 2));
		System.out.println("Therefore moved is " + moved);
		return moved;
	}
	
	public float getTimeElapsed() {
		return /*DisplayManager.getFrameTimeSeconds()*/0.01f;
	}
	
	public float getLastTimeElapsed() {
		return lastTimeElapsed;
	}

	public String toString() {
		return "Ball at (" + getPosition().x + "|" + getPosition().y + "|" + getPosition().z + ") with velocity (" + currentVel.x + "|" + currentVel.y + "|" + currentVel.z + ")" ;
	}
	
	public boolean equals(Object o) {
		return super.equals(o);
	}

}
