package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity {

	private TexturedModel model;
	private Vector3f position;
	protected Vector3f rotVel = new Vector3f();
	private float scale;
	
	private int textureIndex = 0;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.model = model;
		this.position = position;
		this.rotVel.x = rotX;
		this.rotVel.y = rotY;
		System.out.println(rotY);
		this.rotVel.z = rotZ;
		this.scale = scale;
	}
	
	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
            float scale) {
        this.textureIndex = index;
        this.model = model;
        this.position = position;
        this.rotVel.x = rotX;
        this.rotVel.y = rotY;
        this.rotVel.z = rotZ;
        this.scale = scale;
    }
	
	  public float getTextureXOffset(){
	        int column = textureIndex%model.getTexture().getNumberOfRows();
	        return (float)column/(float)model.getTexture().getNumberOfRows();
	    }
	     
	    public float getTextureYOffset(){
	        int row = textureIndex/model.getTexture().getNumberOfRows();
	        return (float)row/(float)model.getTexture().getNumberOfRows();
	    }
	 
	
	public void increasePosition(float dx, float dy, float dz){
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void increasePosition(Vector3f vec){
		this.position.x += vec.x;
		this.position.y += vec.y;
		this.position.z += vec.z;
	}
	
	public void increaseRotation(float dx, float dy, float dz){
		this.rotVel.x += dx;
		this.rotVel.y += dy;
		this.rotVel.z += dz;
		
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotVel.x;
	}

	public void setRotX(float rotX) {
		this.rotVel.x = rotX;
	}

	public float getRotY() {
		return rotVel.y;
	}

	public void setRotY(float rotY) {
		this.rotVel.y = rotY;
	}

	public float getRotZ() {
		return rotVel.z;
	}

	public void setRotZ(float rotZ) {
		this.rotVel.z = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	
}
