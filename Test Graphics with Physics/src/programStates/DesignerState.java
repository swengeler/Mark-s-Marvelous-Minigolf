package programStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import Physics.PhysicsEngine;
import entities.Ball;
import entities.Camera;
import entities.Empty;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;

public class DesignerState implements State{
	
	private Camera camera;
	private Loader loader;
	private Map<String,TexturedModel> tModels = new HashMap<String,TexturedModel>();
	private World world;
	private MasterRenderer renderer;
	private PhysicsEngine mainEngine;
	private ArrayList<Ball> balls = new ArrayList<Ball>();

	public DesignerState(Loader loader){
		init(loader);
	}
	
	@Override
	public void init(Loader loader) {
		this.loader = loader;
		loadModels();
		createBall(new Vector3f(400, 5, 400));
		camera = new Camera(balls.get(0));
		world = new World(camera);
		loadLights();
		renderer = new MasterRenderer(loader, camera);
		mainEngine = new PhysicsEngine(balls, world);
	}

	@Override
	public void renderScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkInputs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}
	
	private void loadModels(){
		
		ModelData tree = OBJFileLoader.loadOBJ("tree");
		ModelData empty = OBJFileLoader.loadOBJ("empty");
		
		RawModel treeModel = loader.loadToVAO(tree.getVertices(), tree.getTextureCoords(), tree.getNormals(), tree.getIndices());
		RawModel emptyModel = loader.loadToVAO(empty.getVertices(), empty.getTextureCoords(), empty.getNormals(), empty.getIndices());
		
		tModels.put("tree", new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("tree"))));
		tModels.put("empty", new TexturedModel(emptyModel, new ModelTexture(loader.loadTexture("tree"))));
	}
	
	public Ball createBall(Vector3f position){
		Ball b = new Empty(tModels.get("empty"), position, 0f, 0f, 0f, 1f);
		balls.add(b);
		return b;
	}
	
	private void loadLights(){
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(1000000,1500000,-1000000),new Vector3f(1f,1f,1f)));
		world.addLights(lights);
	}

}
