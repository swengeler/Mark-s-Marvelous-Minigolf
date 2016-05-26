package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class Terrain {

	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float RADIUS = 15;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private float x;
	private float z;
	private RawModel model;
	private ModelTexture texture;
	
	private float[][] heights;
	
	private int VERTEX_COUNT;
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, boolean rand){
		
		this.texture = texture;
		this.x = gridX * getSize();
		this.z = gridZ * getSize();
		this.model = generateTerrain(loader, rand);
		
	}
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightMap){
		
		this.texture = texture;
		this.x = gridX * getSize();
		this.z = gridZ * getSize();
		this.model = generateTerrain(loader, heightMap);
		
	}
	
	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, float[][] heights){
		
		this.texture = texture;
		this.x = gridX * getSize();
		this.z = gridZ * getSize();
		this.heights = heights;
		this.model = generateTerrain(loader);
		
	}
	
	public float[][] getHeights(){
		return heights;
	}
	
	public float getGridX(){
		return this.x/getSize();
	}
	
	public float getGridZ(){
		return this.z/getSize();
	}
	
	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	

	private RawModel generateTerrain(Loader loader, boolean rand){
		if(rand){
			VERTEX_COUNT = 256;
			HeightsGenerator generator = new HeightsGenerator((int)(x/SIZE),(int)(z/SIZE), VERTEX_COUNT, (int)(Math.random()*100000));
			heights = new float[VERTEX_COUNT][VERTEX_COUNT];
			
			int count = VERTEX_COUNT * VERTEX_COUNT;
			float[] vertices = new float[count * 3];
			float[] normals = new float[count * 3];
			float[] textureCoords = new float[count*2];
			int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
			int vertexPointer = 0;
			for(int i=0;i<VERTEX_COUNT;i++){
				for(int j=0;j<VERTEX_COUNT;j++){
					vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * getSize();
					heights[j][i] = getHeight(j, i, generator);
					vertices[vertexPointer*3+1] = heights[j][i];
					vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * getSize();
					Vector3f normal = calculateNormal(j, i, generator);
					normals[vertexPointer*3] = normal.x;
					normals[vertexPointer*3+1] = normal.y;
					normals[vertexPointer*3+2] = normal.z;
					textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
					textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
					vertexPointer++;
				}
			}
			int pointer = 0;
			for(int gz=0;gz<VERTEX_COUNT-1;gz++){
				for(int gx=0;gx<VERTEX_COUNT-1;gx++){
					int topLeft = (gz*VERTEX_COUNT)+gx;
					int topRight = topLeft + 1;
					int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
					int bottomRight = bottomLeft + 1;
					indices[pointer++] = topLeft;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = topRight;
					indices[pointer++] = topRight;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = bottomRight;
				}
			}
			return loader.loadToVAO(vertices, textureCoords, normals, indices);
		} else {
			VERTEX_COUNT = 512;
			heights = new float[VERTEX_COUNT][VERTEX_COUNT];
			
			int count = VERTEX_COUNT * VERTEX_COUNT;
			float[] vertices = new float[count * 3];
			float[] normals = new float[count * 3];
			float[] textureCoords = new float[count*2];
			int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
			int vertexPointer = 0;
			for(int i=0;i<VERTEX_COUNT;i++){
				for(int j=0;j<VERTEX_COUNT;j++){
					vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * getSize();
					heights[j][i] = 0;
					vertices[vertexPointer*3+1] = 0;
					vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * getSize();
					normals[vertexPointer*3] = 0;
					normals[vertexPointer*3+1] = 1;
					normals[vertexPointer*3+2] = 0;
					textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
					textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
					vertexPointer++;
				}
			}
			int pointer = 0;
			for(int gz=0;gz<VERTEX_COUNT-1;gz++){
				for(int gx=0;gx<VERTEX_COUNT-1;gx++){
					int topLeft = (gz*VERTEX_COUNT)+gx;
					int topRight = topLeft + 1;
					int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
					int bottomRight = bottomLeft + 1;
					indices[pointer++] = topLeft;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = topRight;
					indices[pointer++] = topRight;
					indices[pointer++] = bottomLeft;
					indices[pointer++] = bottomRight;
				}
			}
			return loader.loadToVAO(vertices, textureCoords, normals, indices);
		}
	}
	
	private RawModel generateTerrain(Loader loader, String heightMap){
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * getSize();
				heights[j][i] = getHeight(j,i,image);
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * getSize();
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private RawModel generateTerrain(Loader loader){
		
		
		VERTEX_COUNT = heights.length;
		
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * getSize();
				
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * getSize();
				Vector3f normal = calculateNormal(j, i, heights);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	public void updateTerrain(Loader loader, float x, float z) {
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		float newHeight = 10;
		for(float i = -RADIUS; i <= RADIUS; i++) {
			for (float k = -RADIUS; k  <= RADIUS; k++) {
				if (x + i >= 0 && x + i < VERTEX_COUNT && z + k >= 0 && z + k < VERTEX_COUNT) {
					float distance = (float) Math.sqrt((i*i)+(k*k));
					if (distance <= RADIUS) {
						float height = (float) ((newHeight/2) * -(Math.cos(Math.PI - (Math.PI * (distance/RADIUS))))+(newHeight/2));
						if (height >= MAX_HEIGHT) {
							heights[(int)(x + i)][(int)(z + k)] =  MAX_HEIGHT;
						} else if (height <= -MAX_HEIGHT) {
							heights[(int)(x + i)][(int)(z + k)] =  -MAX_HEIGHT;
						} else {
							if (x + i >= 0 && x + i <= VERTEX_COUNT && z + k >= 0 && z + k <= VERTEX_COUNT) {
								if (height > heights[(int)(x + i)][(int)(z + k)]) {
									heights[(int)(x + i)][(int)(z + k)] =  height;		
								}
							}
						}
					}
				}
				
			}
		}
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * getSize();
				vertices[vertexPointer*3+1] = heights[j][i];
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * getSize();
				normals[vertexPointer*3] = 0;
				normals[vertexPointer*3+1] = 1;
				normals[vertexPointer*3+2] = 0;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		this.model = loader.loadToVAOTerrain(vertices, textureCoords, normals, indices);
		//return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private float getHeight(int x, int z, BufferedImage image){
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getHeight()){
			return 0;
		}
		float height = image.getRGB(x,z);
		height += MAX_PIXEL_COLOR/2f;
		height /= MAX_PIXEL_COLOR/2f;
		height *= MAX_HEIGHT;
		return height;
			
	}
	
	private float getHeight(int x, int z, HeightsGenerator generator){
		return generator.generateHeight(x, z);
			
	}
	
	private Vector3f calculateNormal(int x, int z, BufferedImage image){
		float heightL = getHeight(x-1, z, image);
		float heightR = getHeight(x+1, z, image);
		float heightD = getHeight(x, z-1, image);
		float heightU = getHeight(x, z+1, image);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}
	private Vector3f calculateNormal(int x, int z, HeightsGenerator generator){
		float heightL = getHeight(x-1, z, generator);
		float heightR = getHeight(x+1, z, generator);
		float heightD = getHeight(x, z-1, generator);
		float heightU = getHeight(x, z+1, generator);
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}
	private Vector3f calculateNormal(int x, int z, float[][] heights){
		if (x < 1 || x + 1 >= heights.length || z < 1 || z + 1 >= heights[0].length) return new Vector3f(0,0,0);
		float heightL = heights[x-1][z];
		float heightR = heights[x+1][z];
		float heightD = heights[x][z-1];
		float heightU = heights[x][z+1];
		Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
		normal.normalise();
		return normal;
	}

	public static float getSize() {
		return SIZE;
	}
	
	public float getVertexCount() {
		return VERTEX_COUNT;
	}

}
