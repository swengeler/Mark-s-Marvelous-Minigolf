package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.World;
import Physics.PhysicsEngine;

public class Ball extends Entity{
	private static final float RUN_SPEED = 2;
	private static final float TURN_SPEED = 100;
	private static final float JUMP_POWER = 40;
	private static final float MIN_XVEL = 0;
	private static final float MIN_YVEL = 0;
	private static final float MIN_ZVEL = 0;
	private static final float COEFF_RESTITUTION = 0.83f;
	
	private Vector3f currentVel = new Vector3f();
	private Vector3f currentAcc = new Vector3f();
	private float currentTurnSpeed = 0;
	
	public Ball(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(World world){
		checkInputs(world);
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		currentAcc = new Vector3f(PhysicsEngine.GRAVITY.x, PhysicsEngine.GRAVITY.y, PhysicsEngine.GRAVITY.z);
		/*
		if(getPosition().y <= world.getHeightOfTerrain(getPosition().x, getPosition().z)){
			Vector3f antinorm = (Vector3f) world.getNormalOfTerrain(getPosition().x, getPosition().z).negate();
			antinorm.normalise(antinorm);
			Vector3f normComponent = (Vector3f) antinorm.scale(Vector3f.dot(currentAcc, antinorm));
			normComponent.negate();
			Vector3f.add(currentAcc, normComponent, currentAcc);
			System.out.println("Acceleration: x=" + currentAcc.x + " y=" + currentAcc.y + " z=" + currentAcc.z);
		}*/
		currentAcc.scale(DisplayManager.getFrameTimeSeconds());
		
		Vector3f.add(currentVel, currentAcc, currentVel);
		
		Vector3f delta = new Vector3f(currentVel.x, currentVel.y, currentVel.z);
		delta.scale(DisplayManager.getFrameTimeSeconds());
		
		super.increasePosition(delta);
		
		float terrainHeight = world.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
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

	public void checkGroundCollision(World world) {
		float terrainHeight = world.getHeightOfTerrain(this.getPosition().x, this.getPosition().z);
		if(this.getPosition().y < terrainHeight ){
			super.getPosition().y = terrainHeight;
			Vector3f normal = world.getNormalOfTerrain(getPosition().x, getPosition().z);
			Vector3f newPartialVel = (Vector3f) normal.scale(2*Vector3f.dot(currentVel, normal));
			Vector3f.sub(newPartialVel, currentVel, currentVel);
			currentVel.negate();
			currentVel.scale(COEFF_RESTITUTION);
		}
		
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
	
}