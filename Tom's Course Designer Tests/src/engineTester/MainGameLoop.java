package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Physics.PhysicsEngine;
import entities.Ball;
import entities.Camera;
import entities.Empty;
import entities.Entity;
import entities.Light;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
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
		ModelData empty = OBJFileLoader.loadOBJ("empty");
		
		RawModel humanModel = loader.loadToVAO(human.getVertices(), human.getTextureCoords(), human.getNormals(), human.getIndices());
		RawModel ballModel = loader.loadToVAO(ball.getVertices(), ball.getTextureCoords(), ball.getNormals(), ball.getIndices());
		RawModel treeModel = loader.loadToVAO(tree.getVertices(), tree.getTextureCoords(), tree.getNormals(), tree.getIndices());
		RawModel fernModel = loader.loadToVAO(fern.getVertices(), fern.getTextureCoords(), fern.getNormals(), fern.getIndices());
		RawModel grassModel = loader.loadToVAO(grass.getVertices(), grass.getTextureCoords(), grass.getNormals(), grass.getIndices());
		RawModel pineModel = loader.loadToVAO(pine.getVertices(), pine.getTextureCoords(), pine.getNormals(), pine.getIndices());
		RawModel boxModel = loader.loadToVAO(box.getVertices(), box.getTextureCoords(), box.getNormals(), box.getIndices());
		RawModel flowerModel = loader.loadToVAO(flower.getVertices(), flower.getTextureCoords(), flower.getNormals(), flower.getIndices());
		RawModel emptyModel = loader.loadToVAO(empty.getVertices(), empty.getTextureCoords(), empty.getNormals(), empty.getIndices());
		
		TexturedModel humanTModel = new TexturedModel(humanModel,new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel ballTModel = new TexturedModel(ballModel,new ModelTexture(loader.loadTexture("white")));
		TexturedModel treeTModel = new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernTModel = new TexturedModel(fernModel,new ModelTexture(loader.loadTexture("fernAtlas")));
		TexturedModel grassTModel = new TexturedModel(grassModel,new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel pineTModel = new TexturedModel(pineModel,new ModelTexture(loader.loadTexture("pine")));
		TexturedModel boxTModel = new TexturedModel(boxModel,new ModelTexture(loader.loadTexture("box")));
		TexturedModel flowerTModel = new TexturedModel(flowerModel,new ModelTexture(loader.loadTexture("flower")));
		TexturedModel emptyTModel = new TexturedModel(emptyModel, new ModelTexture(loader.loadTexture("flower")));
		
		
		fernTModel.getTexture().setNumberOfRows(2);
		fernTModel.getTexture().setHasTransparency(true);
		grassTModel.getTexture().setUseFakeLighting(true);
		grassTModel.getTexture().setHasTransparency(true);
		flowerTModel.getTexture().setUseFakeLighting(true);
		flowerTModel.getTexture().setHasTransparency(true);
		
		ballTModel.getTexture().setShineDamper(10);
		ballTModel.getTexture().setReflectivity(1);
		
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(0,1000,400),new Vector3f(1,1,1)));
		
		Ball player1 = new Empty(emptyTModel, new Vector3f(400, 5, 400), 0, 0, 0, 1);
		List<Ball> balls = new ArrayList<Ball>();
		balls.add(player1);
		Camera camera = new Camera(player1);
		World world = new World(camera);
		world.add(new Terrain(0, 0, loader,new ModelTexture(loader.loadTexture("grass"))));
		
		List<Entity> nature = new ArrayList<Entity>();

		MasterRenderer renderer = new MasterRenderer(loader);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		world.addEntities(nature);
		world.addLights(lights);
		
		PhysicsEngine mainEngine = new PhysicsEngine(balls, world);
		
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();

		while(!Display.isCloseRequested()){
			player1.move(world);
			world.start();
			picker.update();
			mainEngine.tick();
			float mouseWheel = Mouse.getDWheel() / 120;
			if (mouseWheel != 0) {
				System.out.println(Mouse.getDWheel() + " " + mouseWheel);
				world.getTerrains().get(0).updateTerrain(loader, ((picker.getCurrentTerrainPoint().x / (Terrain.getSize()/2)) * 256), ((picker.getCurrentTerrainPoint().z / (Terrain.getSize()/2)) * 256), mouseWheel );
			}
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - 0);
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, 1, 0, 0));
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			
			fbos.bindRefractionFrameBuffer();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, -1, 0, 0));
			
			
			fbos.unbindCurrentFrameBuffer();
			renderer.processEntity(player1);
			renderer.processWorld(world, new Vector4f(0, -1, 0, 10000));
			waterRenderer.render(waters, camera);
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		
	}

}
