package entities;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.World;
import Physics.PhysicalFace;
import Physics.PhysicsEngine;

public class Ball extends Entity{
	private static final float FACTOR = 1;
	private static final float RUN_SPEED = 2;
	private static final float TURN_SPEED = 100;
	private static final float JUMP_POWER = 40;
	private static final float MIN_XVEL = 0;
	private static final float MIN_YVEL = 0;
	private static final float MIN_ZVEL = 0;
	
	public static final float RADIUS_IN_M = 0.04267f;
	public static final float RADIUS = 1f;

	private Vector3f currentVel = new Vector3f();
	private Vector3f currentAcc = new Vector3f();
	private float currentTurnSpeed = 0;

	private boolean moving;

	public Ball(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.moving = true;
	}

	public void move(){ // world really necessary?
		//checkInputs(world);
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		Vector3f gravity = new Vector3f(0, PhysicsEngine.GRAVITY.y, 0);
		gravity = (Vector3f) gravity.scale(DisplayManager.getFrameTimeSeconds());
		System.out.println("DisplayManager.getFrameTimeSeconds(): " + DisplayManager.getFrameTimeSeconds());

		Vector3f.add(currentVel, gravity, currentVel);

		Vector3f delta = new Vector3f(currentVel.x, currentVel.y, currentVel.z);
		delta.scale(DisplayManager.getFrameTimeSeconds());

		super.increasePosition(delta);
		System.out.println("Ball's position after moving: (" + getPosition().x  + "|" + getPosition().y + "|" + getPosition().z + ")");
	}

	public void setMoving(boolean moving) {
		System.out.println("Moving set to " + moving);
		this.moving = moving;
	}

	public boolean isMoving() {
		return moving;
	}

	private void jump(){
		setMoving(true);
		this.currentVel.y = JUMP_POWER;
		System.out.println("Moving set to " + moving + " (velocity now: (" + currentVel.x + "|" + currentVel.y + "|" + currentVel.z + ")");
	}

	public void checkInputs(){
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.currentVel.x += (float) (RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())))/FACTOR;
			this.currentVel.z += (float) (RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())))/FACTOR;
			setMoving(true);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentVel.x += (float) -(RUN_SPEED * Math.sin(Math.toRadians(super.getRotY())))/FACTOR;
			this.currentVel.z += (float) -(RUN_SPEED * Math.cos(Math.toRadians(super.getRotY())))/FACTOR;
			setMoving(true);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			this.currentVel.x += 5;
			System.out.println("x-speed increased by pressing up-arrow");
			setMoving(true);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			this.currentVel.x -= 5;
			System.out.println("x-speed decreased by pressing down-arrow");
			setMoving(true);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_1)) {
			this.currentVel.z = -100;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_2)) {
			this.currentVel.z = -200;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_3)) {
			this.currentVel.z = -300;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_4)) {
			this.currentVel.z = -400;
		} else if (Keyboard.isKeyDown(Keyboard.KEY_T) && Keyboard.isKeyDown(Keyboard.KEY_5)) {
			this.currentVel.z = -500;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if (Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else
			this.currentTurnSpeed = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			jump();
	}

	public boolean collidesWith(ArrayList<PhysicalFace> faces) {
		//System.out.println("Check for collision with " + faces.size() + " faces in the Ball class");
		for (PhysicalFace f : faces) {
			if (f.collidesWithBall(this))
				return true;
		}
		return false;
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

	public Vector3f getVelocity() {
		return currentVel;
	}
	
	public void setVelocity(Vector3f v) {
		currentVel.set(v.x, v.y, v.y);
	}

}
