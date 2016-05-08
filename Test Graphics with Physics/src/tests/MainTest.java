package tests;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class MainTest {

	public static void main(String[] args) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		//Matrix4f.translate(new Vector3f(1f,1f,1f), matrix, matrix);
		//System.out.println("Transformation: translate vector by (1|1|1)");
		//Matrix4f.rotate((float) Math.toRadians(360), new Vector3f(1,0,0), matrix, matrix);
		//System.out.println("Transformation: rotate vector around the x-axis by 90 degrees");
		//Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		//Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		//Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		System.out.println("Transformation: rotate vector around the x-axis by 90 degrees");
		
		Vector4f v = new Vector4f(0.5f,0.5f,0.5f,1);
		System.out.println("Vector before transformation: (" + v.x + "|" + v.y + "|" + v.z + ")");
		Matrix4f.transform(matrix, v, v);
		System.out.println("Vector after transformation: (" + v.x + "|" + v.y + "|" + v.z + ")");
		
		
	}

}
