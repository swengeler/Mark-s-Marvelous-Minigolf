package programStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Physics.PhysicsEngine;
import entities.Ball;
import entities.Camera;
import entities.Empty;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
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
	private GuiRenderer guiRenderer;
	private ArrayList<GuiTexture> guis;
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	
	private boolean water = false;
	private boolean particle = false;
	private boolean shadow = true;
	private boolean normalMap = false;

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
		createTerrain(0, 0, "grass", true);
	}

	@Override
	public void renderScreen() {
		if(shadow){
			renderer.renderShadowMap(world.getEntities(), world.getLights().get(0));
		}
		
		for(Ball b:balls)
			renderer.processEntity(b);
		renderer.processWorld(world, new Vector4f(0, -1, 0, 10000), false);
		
		//ParticleMaster.renderParticles(camera);
		//ParticleMaster.update(camera);
		//guiRenderer.render(guis);
		
	}

	@Override
	public void checkInputs() {
		balls.get(0).checkInputs(world);
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		//fbos.cleanUp();
		//guiRenderer.cleanUp();
		//waterRenderer.getShader().cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		//ParticleMaster.cleanUp();
		
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
	
	public Terrain createTerrain(int gridX, int gridY, String texName, boolean rand){
		Terrain t = new Terrain(gridX, gridY, loader, new ModelTexture(loader.loadTexture(texName)), rand);
		world.add(t);
		return t;
	}
	
	public Terrain createTerrain(int gridX, int gridY, String texName, String heightMap){
		Terrain t = new Terrain(gridX, gridY, loader, new ModelTexture(loader.loadTexture(texName)), heightMap);
		world.add(t);
		return t;
	}

}
