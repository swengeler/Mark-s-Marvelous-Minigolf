package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Physics.PhysicsEngine;
import entities.Ball;
import entities.Camera;
import entities.Entity;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.ModelDataNM;
import normalMappingObjConverter.NormalMappedObjLoader;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.EntityRenderer;
import shaders.StaticShader;
import terrains.Terrain;
import terrains.World;
import textures.ModelTexture;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop {

	public static void main(String[] args) {
	
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		
		ModelData human = OBJFileLoader.loadOBJ("person");
		ModelData ball = OBJFileLoader.loadOBJ("ball_oth_high");
		ModelData tree = OBJFileLoader.loadOBJ("tree");
		ModelData fern = OBJFileLoader.loadOBJ("fern");
		ModelData grass = OBJFileLoader.loadOBJ("grassModel");
		ModelData pine = OBJFileLoader.loadOBJ("pine");
		ModelData flower = OBJFileLoader.loadOBJ("grassModel");
		ModelData box = OBJFileLoader.loadOBJ("box");
		
		
		RawModel humanModel = loader.loadToVAO(human.getVertices(), human.getTextureCoords(), human.getNormals(), human.getIndices());
		RawModel ballModel = loader.loadToVAO(ball.getVertices(), ball.getTextureCoords(), ball.getNormals(), ball.getIndices());
		RawModel treeModel = loader.loadToVAO(tree.getVertices(), tree.getTextureCoords(), tree.getNormals(), tree.getIndices());
		RawModel fernModel = loader.loadToVAO(fern.getVertices(), fern.getTextureCoords(), fern.getNormals(), fern.getIndices());
		RawModel grassModel = loader.loadToVAO(grass.getVertices(), grass.getTextureCoords(), grass.getNormals(), grass.getIndices());
		RawModel pineModel = loader.loadToVAO(pine.getVertices(), pine.getTextureCoords(), pine.getNormals(), pine.getIndices());
		RawModel boxModel = loader.loadToVAO(box.getVertices(), box.getTextureCoords(), box.getNormals(), box.getIndices());
		RawModel flowerModel = loader.loadToVAO(flower.getVertices(), flower.getTextureCoords(), flower.getNormals(), flower.getIndices());
		
		TexturedModel humanTModel = new TexturedModel(humanModel,new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel ballTModel = new TexturedModel(ballModel,new ModelTexture(loader.loadTexture("white")));
		TexturedModel treeTModel = new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernTModel = new TexturedModel(fernModel,new ModelTexture(loader.loadTexture("fernAtlas")));
		TexturedModel grassTModel = new TexturedModel(grassModel,new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel pineTModel = new TexturedModel(pineModel,new ModelTexture(loader.loadTexture("pine")));
		TexturedModel boxTModel = new TexturedModel(boxModel,new ModelTexture(loader.loadTexture("box")));
		TexturedModel flowerTModel = new TexturedModel(flowerModel,new ModelTexture(loader.loadTexture("flower")));
		TexturedModel barrelTModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel")));
		TexturedModel crateTModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), new ModelTexture(loader.loadTexture("crate")));
		TexturedModel boulderTModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), new ModelTexture(loader.loadTexture("boulder")));
		
		fernTModel.getTexture().setNumberOfRows(2);
		fernTModel.getTexture().setHasTransparency(true);
		grassTModel.getTexture().setUseFakeLighting(true);
		grassTModel.getTexture().setHasTransparency(true);
		flowerTModel.getTexture().setUseFakeLighting(true);
		flowerTModel.getTexture().setHasTransparency(true);
		
		barrelTModel.getTexture().setShineDamper(10);
		barrelTModel.getTexture().setReflectivity(0.3f);
		barrelTModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		
		crateTModel.getTexture().setShineDamper(10);
		crateTModel.getTexture().setReflectivity(0.3f);
		crateTModel.getTexture().setNormalMap(loader.loadTexture("crateNormal"));
		
		boulderTModel.getTexture().setShineDamper(10);
		boulderTModel.getTexture().setReflectivity(0.3f);
		boulderTModel.getTexture().setNormalMap(loader.loadTexture("boulderNormal"));
		
		ballTModel.getTexture().setShineDamper(10);
		ballTModel.getTexture().setReflectivity(1);
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(400,1000,400),new Vector3f(0.4f,0.6f,0.6f)));
		//lights.add(new Light(new Vector3f(70,10,0),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(35,17,35),new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(0,7,70),new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));
		
		Ball player1 = new Ball(ballTModel, new Vector3f(50, 3, 0), 0, 0, 0, 0.5f);
		List<Ball> balls = new ArrayList<Ball>();
		balls.add(player1);
		
		
		Camera camera = new Camera(player1);
		World world = new World(camera);
		world.add(new Terrain(0, 0, loader,new ModelTexture(loader.loadTexture("grass")), "heightmap"));
		
		List<Entity> nature = new ArrayList<Entity>();
		Random r = new Random();
		for(int i=0; i<200; i++){
			if(i<40){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(humanTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 1));
			}
			if(i<5){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(ballTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 2));
			}
			if(i<100){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(treeTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 7));
			}
			if(i<120){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(fernTModel, r.nextInt(4),new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 1));
			}
			if(i<120){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(grassTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 1));
			}
			if(i<60){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(pineTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 1));
			}
			if(i<2){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(boxTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 10));
			}
			if(i<200){
				float x = r.nextFloat()*Terrain.getSize();
				float z = r.nextFloat()*Terrain.getSize();
				nature.add(new Entity(flowerTModel, new Vector3f(x, world.getHeightOfTerrain(x, z), z), 0, r.nextFloat()*180, 0, 1));
			}
		}
		
		List<Entity> dragons = new ArrayList<Entity>();
		for(int i=0; i<10; i++){
			float x = (float) (Math.random() * 200 - 100);
			float y = (float) (Math.random() * 200 - 100);
			float z = (float) (Math.random() * -300);
			//dragons.add(new Entity(staticModel, new Vector3f(x, y, z), 0f, (float) (Math.random() * 180f), 0f, 1f ));
		}
		Entity big_Human = new Entity(humanTModel, new Vector3f(0,0,50), 0f, 0f, 0f, 10);
		
		MasterRenderer renderer = new MasterRenderer(loader);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("fernAtlas"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		world.addEntities(nature);
		world.addLights(lights);
		
		PhysicsEngine mainEngine = new PhysicsEngine(balls, world);
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(75, 120, 0));
		
		List<Entity> normalMapEntities = new ArrayList<Entity>();
		normalMapEntities.add(new Entity(barrelTModel, new Vector3f(20, 20, 20), 0f, 0f, 0f, 1));
		normalMapEntities.add(new Entity(crateTModel, new Vector3f(40, 20, 20), 0f, 0f, 0f, 0.05f));
		normalMapEntities.add(new Entity(boulderTModel, new Vector3f(60, 20, 20), 0f, 0f, 0f, 1));
		world.addNormE(normalMapEntities);
		
		
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleStar"), 1);
		ParticleSystem system = new ParticleSystem(particleTexture, 40, 10, -0.5f, 3, 1.6f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.25f);
		system.setScaleError(0.5f);
		system.randomizeRotation();
		
		while(!Display.isCloseRequested()){
			player1.move(world);
			world.start();
			picker.update();
			mainEngine.tick();
			normalMapEntities.get(0).increaseRotation(0, 30*DisplayManager.getFrameTimeSeconds(), 0);
			normalMapEntities.get(1).increaseRotation(0, 60*DisplayManager.getFrameTimeSeconds(), 0);
			normalMapEntities.get(2).increaseRotation(0, 120*DisplayManager.getFrameTimeSeconds(), 0);
			
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if(terrainPoint != null){
				
			}system.generateParticles(player1.getPosition());
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, 1, 0, -waters.get(0).getHeight()));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			
			fbos.bindRefractionFrameBuffer();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, -1, 0, waters.get(0).getHeight()));
			
			
			fbos.unbindCurrentFrameBuffer();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, -1, 0, 10000));
			waterRenderer.render(waters, camera);
			ParticleMaster.renderParticles(camera);
			ParticleMaster.update();
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		ParticleMaster.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
