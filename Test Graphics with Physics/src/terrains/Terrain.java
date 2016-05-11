package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Physics.CollisionData;
import Physics.PhysicalFace;
import entities.Ball;
import models.RawModel;
import objConverter.ModelData;
import renderEngine.Loader;
import textures.ModelTexture;
import toolbox.Maths;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class Terrain {

	private static final float SIZE = 800;
	private static final int VERTEX_COUNT = 512;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	//private static float MAX_HEIGHT; // when constructing all terrains, check for the maximum height, if the ball is above that no point in checking
	// maybe make individual max_heights for one terrain!!

	private float x;
	private float z;
	private float maxHeight;
	private RawModel model;
	private ModelTexture texture;
	private CollisionData cdata;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture){

		this.texture = texture;
		this.x = gridX * getSize();
		this.z = gridZ * getSize();
		this.model = generateTerrain(loader);

	}

	public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture, String heightMap){

		this.texture = texture;
		this.x = gridX * getSize();
		this.z = gridZ * getSize();
		this.model = generateTerrain(loader, heightMap);

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

	private RawModel generateTerrain(Loader loader) {

		heights = new float[VERTEX_COUNT][VERTEX_COUNT];

		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++) {
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
		for(int gz=0;gz<VERTEX_COUNT-1;gz++) {
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

	private RawModel generateTerrain(Loader loader, String heightMap){

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int VERTEX_COUNT = image.getHeight();
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
	
	public boolean ballInTerrain(Ball b) {
		float ballR = b.getRadius();
		float ballX = b.getPosition().x;
		float ballZ = b.getPosition().z;
		return  (ballX - ballR) < (this.x + Terrain.SIZE) && (ballX + ballR) > this.x &&
				(ballZ - ballR) < (this.z + Terrain.SIZE) && (ballZ + ballR) > this.z; // other size? e.g. heights.length?
	}

	public ArrayList<PhysicalFace> getCollidingFaces(Ball b) {
		System.out.println("getCollidingFaces in Terrain is called");
		float ballR = b.getRadius();
		float ballX = b.getPosition().x - this.x;
		float ballZ = b.getPosition().z - this.z;
		
		if ((b.getPosition().y - ballR) > this.maxHeight)
			return new ArrayList<PhysicalFace>(0);

		int leftX = (int) Math.floor(ballX - ballR);
		if (leftX < 0) 
			leftX = 0;
		int rightX = (int) Math.ceil(ballX + ballR);
		if (rightX >= heights[0].length)
			rightX = heights[0].length - 1;
		int upperZ = (int) Math.floor(ballZ - ballR);
		if (upperZ < 0)
			upperZ = 0;
		int lowerZ = (int) Math.ceil(ballZ + ballR);
		if (lowerZ >= heights.length)
			lowerZ = heights.length - 1;
		
		System.out.println("leftX = " + leftX + ", rightX = " + rightX + ", upperZ = " + upperZ + ", lowerZ = " + lowerZ);

		Vector3f p1 = new Vector3f(0,0,0), p2 = new Vector3f(0,0,0), p3 = new Vector3f(0,0,0), normal = new Vector3f(0,0,0), v1 = new Vector3f(0,0,0), v2 = new Vector3f(0,0,0);

		ArrayList<PhysicalFace> collidingFaces = new ArrayList<PhysicalFace>();
		for (int i = leftX; i < rightX - 1 && i < heights.length - 1; i++) {
			for (int j = upperZ; i < lowerZ - 1 && j < heights[0].length - 1; j++) {
				// upper left corner
				p1.set(i + this.x, this.heights[i][j], j + this.z);
				// lower left corner
				p2.set(i + this.x + 1, this.heights[i + 1][j], j + this.z);
				// upper right corner
				p3.set(i + this.x, this.heights[i][j + 1], j + this.z + 1);

				Vector3f.sub(p2, p1, v1);
				Vector3f.sub(p3, p1, v2);
				Vector3f.cross(v1, v2, normal);
				normal.normalise();
				collidingFaces.add(new PhysicalFace(normal,p1,p2,p3));

				// upper right corner
				p1.set(i + this.x, this.heights[i][j + 1], j + this.z + 1);
				// lower left corner
				p2.set(i + this.x + 1, this.heights[i + 1][j], j + this.z);
				// lower right corner
				p3.set(i + this.x + 1, this.heights[i + 1][j + 1], j + this.z + 1);

				Vector3f.sub(p2, p1, v1);
				Vector3f.sub(p3, p1, v2);
				Vector3f.cross(v1, v2, normal);
				normal.normalise();
				collidingFaces.add(new PhysicalFace(normal,p1,p2,p3));
			}
		}
		System.out.println("Number of colliding faces in terrain: " + collidingFaces.size());
		return collidingFaces;
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

	public static float getSize() {
		return SIZE;
	}

}
