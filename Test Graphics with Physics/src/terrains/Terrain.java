package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Physics.CollisionData;
import Physics.PhysicalFace;
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
	
	private float x;
	private float z;
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
		createCollisionData(vertices,indices);
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
		createCollisionData(vertices,indices);
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}
	
	private void createCollisionData(float[] ver, int[] ind) {
		long before = System.currentTimeMillis();
		cdata = new CollisionData();
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(1,1,1),1,1,1,1);
		Vector4f tfVector = new Vector4f(0,0,0,1f);
		Vector3f p1 = new Vector3f(0,0,0), p2 = new Vector3f(0,0,0), p3 = new Vector3f(0,0,0);
		Vector3f normal = new Vector3f(0,0,0), v1 = new Vector3f(0,0,0), v2 = new Vector3f(0,0,0);

		float minX = Float.MAX_VALUE;
		float minY = minX;
		float minZ = minX;
		float maxX = Float.MIN_VALUE;
		float maxY = maxX;
		float maxZ = maxX;

		PhysicalFace face;
		int[] currInd = new int[3];
		for (int i = 0; i < (ind.length / 3); i += 3) {
			currInd[0] = ind[i] * 3;
			currInd[1] = ind[i + 1] * 3;
			currInd[2] = ind[i + 2] * 3;

			// first vertex
			tfVector.set(ver[currInd[0]], ver[currInd[0] + 1], ver[currInd[0] + 2]);
			Matrix4f.transform(transformationMatrix, tfVector, tfVector);
			p1.set(tfVector.x, tfVector.y, tfVector.z);
			// second vertex
			tfVector.set(ver[currInd[1]], ver[currInd[1] + 1], ver[currInd[1] + 2]);
			Matrix4f.transform(transformationMatrix, tfVector, tfVector);
			p2.set(tfVector.x, tfVector.y, tfVector.z);
			// third vertex
			tfVector.set(ver[currInd[2]], ver[currInd[2] + 1], ver[currInd[2] + 2]);
			Matrix4f.transform(transformationMatrix, tfVector, tfVector);
			p3.set(tfVector.x, tfVector.y, tfVector.z);
			
			// adjusting max/min values
			minX = Math.min(minX, Math.min(p1.x, Math.min(p2.x, p3.x)));
			minY = Math.min(minY, Math.min(p1.y, Math.min(p2.y, p3.y)));
			minZ = Math.min(minZ, Math.min(p1.z, Math.min(p2.z, p3.z)));
			maxX = Math.max(maxX, Math.max(p1.x, Math.max(p2.x, p3.x)));
			maxY = Math.max(maxY, Math.max(p1.y, Math.max(p2.y, p3.y)));
			maxZ = Math.max(maxZ, Math.max(p1.z, Math.max(p2.z, p3.z)));
			
			// constructing a face from the three points p1, p2 and p3 and their resulting normal
			Vector3f.sub(p2, p1, v1);
			Vector3f.sub(p3, p1, v2);
			Vector3f.cross(v1, v2, normal);
			normal.normalise();
			face = new PhysicalFace(normal, p1, p2, p3);

			cdata.addFace(face);
		}
		cdata.setBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		long after = System.currentTimeMillis();
		long difference = after - before;
		System.out.println("Time to construct faces (for terrain): " + difference + "\n");
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
