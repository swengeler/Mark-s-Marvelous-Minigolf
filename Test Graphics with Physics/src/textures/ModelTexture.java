package textures;

public class ModelTexture {

	private int textureID;
	
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighing = false;
	
	public ModelTexture(int id){
		textureID = id;
	}
	
	

	public boolean isUseFakeLighing() {
		return useFakeLighing;
	}



	public void setUseFakeLighing(boolean useFakeLighing) {
		this.useFakeLighing = useFakeLighing;
	}



	public boolean isHasTransparency() {
		return hasTransparency;
	}



	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}



	public int getID() {
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	
}
