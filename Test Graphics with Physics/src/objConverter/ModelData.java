package objConverter;

import java.io.PrintWriter;

public class ModelData {
	
	public static final int PRINT_DATA_FILE = 0;
	public static final int PRINT_NRFACES = 1;

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private float furthestPoint;

	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices,
			float furthestPoint) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.furthestPoint = furthestPoint;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public float getFurthestPoint() {
		return furthestPoint;
	}
	
	public void print(int id) {
		try {
			if (id == ModelData.PRINT_DATA_FILE) {
				PrintWriter writer = new PrintWriter("printed_vertices_and_shit.txt","UTF-8");
				writer.println("VERTICES AND NORMALS");
				for (int i = 0; i < (vertices.length / 3); i++) {
					writer.println(i + ") Vertex: ( " + vertices[i] + " | " + vertices[i + 1] + " | " + vertices[i + 2] + " ) --- Normal: ( " + normals[i] + " | " + normals[i + 1] + " | " + normals[i + 2] + " )");
				}
				writer.println("\nINDICES");
				for (int i = 0; i < (indices.length / 3); i++) {
					writer.println(i + ") Vertices: ( " + indices[i] + " | " + indices[i + 1] + " | " + indices[i + 2] + ")");
				}
				writer.close();
			} else if (id == ModelData.PRINT_NRFACES) {
				System.out.println("\nNumber of faces: " + (indices.length / 3) + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
