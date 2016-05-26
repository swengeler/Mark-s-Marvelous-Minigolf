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

import Physics.BoundingBox;
import Physics.PhysicsEngine;
import Physics.ShotData;
import entities.RealBall;
import entities.Ball;
import entities.Camera;
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

	private static int counter;

	public static void main(String[] args) {

		long before = System.currentTimeMillis();

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		ModelData human = OBJFileLoader.loadOBJ("person");
		ModelData ball = OBJFileLoader.loadOBJ("ball_centred_hight_scaled2");
		ModelData tree = OBJFileLoader.loadOBJ("tree");
		ModelData fern = OBJFileLoader.loadOBJ("fern");
		ModelData grass = OBJFileLoader.loadOBJ("grassModel");
		ModelData pine = OBJFileLoader.loadOBJ("pine");
		ModelData flower = OBJFileLoader.loadOBJ("grassModel");
	    ModelData box = OBJFileLoader.loadOBJ("cube_rounded");
	    ModelData dragon = OBJFileLoader.loadOBJ("dragon");
	    ModelData dragon_low = OBJFileLoader.loadOBJ("dragon_low_test");
	    ModelData flag = OBJFileLoader.loadOBJ("hole");
	    ModelData cone = OBJFileLoader.loadOBJ("cone");
	    ModelData pyramid = OBJFileLoader.loadOBJ("ramp");
	    ModelData wall = OBJFileLoader.loadOBJ("wall_segment");
		box.print(ModelData.PRINT_DATA_FILE);

		RawModel humanModel = loader.loadToVAO(human.getVertices(), human.getTextureCoords(), human.getNormals(), human.getIndices());
		RawModel ballModel = loader.loadToVAO(ball.getVertices(), ball.getTextureCoords(), ball.getNormals(), ball.getIndices());
		RawModel treeModel = loader.loadToVAO(tree.getVertices(), tree.getTextureCoords(), tree.getNormals(), tree.getIndices());
		RawModel fernModel = loader.loadToVAO(fern.getVertices(), fern.getTextureCoords(), fern.getNormals(), fern.getIndices());
		RawModel grassModel = loader.loadToVAO(grass.getVertices(), grass.getTextureCoords(), grass.getNormals(), grass.getIndices());
		RawModel pineModel = loader.loadToVAO(pine.getVertices(), pine.getTextureCoords(), pine.getNormals(), pine.getIndices());
		RawModel boxModel = loader.loadToVAO(box.getVertices(), box.getTextureCoords(), box.getNormals(), box.getIndices());
		RawModel flowerModel = loader.loadToVAO(flower.getVertices(), flower.getTextureCoords(), flower.getNormals(), flower.getIndices());
		RawModel dragonModel = loader.loadToVAO(dragon.getVertices(), dragon.getTextureCoords(), dragon.getNormals(), dragon.getIndices());
		RawModel flagModel = loader.loadToVAO(flag.getVertices(), flag.getTextureCoords(), flag.getNormals(), flag.getIndices());
		RawModel coneModel = loader.loadToVAO(cone.getVertices(), cone.getTextureCoords(), cone.getNormals(), cone.getIndices());
		RawModel pyramidModel = loader.loadToVAO(pyramid.getVertices(), pyramid.getTextureCoords(), pyramid.getNormals(), pyramid.getIndices());
		RawModel wallModel = loader.loadToVAO(wall.getVertices(), wall.getTextureCoords(), wall.getNormals(), wall.getIndices());

		TexturedModel humanTModel = new TexturedModel(humanModel,new ModelTexture(loader.loadTexture("playerTexture")));
		TexturedModel ballTModel = new TexturedModel(ballModel,new ModelTexture(loader.loadTexture("white")));
		TexturedModel treeTModel = new TexturedModel(treeModel,new ModelTexture(loader.loadTexture("tree")));
		TexturedModel fernTModel = new TexturedModel(fernModel,new ModelTexture(loader.loadTexture("fernAtlas")));
		TexturedModel grassTModel = new TexturedModel(grassModel,new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel pineTModel = new TexturedModel(pineModel,new ModelTexture(loader.loadTexture("pine")));
		TexturedModel boxTModel = new TexturedModel(boxModel,new ModelTexture(loader.loadTexture("brick_wall")));
		TexturedModel flowerTModel = new TexturedModel(flowerModel,new ModelTexture(loader.loadTexture("flower")));
		TexturedModel dragonTModel = new TexturedModel(dragonModel,new ModelTexture(loader.loadTexture("white")));
		TexturedModel flagTModel = new TexturedModel(flagModel,new ModelTexture(loader.loadTexture("metal2")));
		TexturedModel coneTModel = new TexturedModel(coneModel,new ModelTexture(loader.loadTexture("blue")));
		TexturedModel pyramidTModel = new TexturedModel(pyramidModel,new ModelTexture(loader.loadTexture("skull_texture")));
		TexturedModel wallTModel = new TexturedModel(wallModel,new ModelTexture(loader.loadTexture("white")));


		long beforeHumans = System.currentTimeMillis();
		//Entity h = new Entity(humanTModel, human, new Vector3f(0, 0, 0), 0, 0, 0, 1, "human");
		Entity a = new Entity(ballTModel, ball, new Vector3f(0, 0, 0), 0, 0, 0, 1);
		Entity b = new Entity(treeTModel, tree, new Vector3f(0, 0, 0), 0, 0, 0, 1, "tree");
		Entity c = new Entity(fernTModel, fern, new Vector3f(0, 0, 0), 0, 0, 0, 1, "fern");
		Entity d = new Entity(grassTModel, grass, new Vector3f(0, 0, 0), 0, 0, 0, 1, "grass");
		Entity e = new Entity(boxTModel, box, new Vector3f(0, 0, 0), 0, 0, 0, 1, "box");
		Entity f = new Entity(flagTModel, flag, new Vector3f(300, 0f, 300), 0, 0, 0, 1, "flag");
		Entity g = new Entity(coneTModel, cone, new Vector3f(100, 0, 300), 0, 0, 0, 5, "cone");
		Entity h = new Entity(pyramidTModel, pyramid, new Vector3f(400, -0.1f, 400), 0, 0, 0, 10, "pyramid");
		Entity i = new Entity(wallTModel, wall, new Vector3f(100, 0f, 100), 0, 0, 0, 1, "wall");
		long afterHumans = System.currentTimeMillis();
		System.out.println("Time to load entities: " + (afterHumans - beforeHumans));


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
		//lights.add(new Light(new Vector3f(70,10,0),new Vector3f(2,0,0), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(35,17,35),new Vector3f(0,2,2), new Vector3f(1,0.01f,0.002f)));
		//lights.add(new Light(new Vector3f(0,7,70),new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f)));

		RealBall player1 = new RealBall(ballTModel, new Vector3f(200, 25, 400), 0, 0, 0, 1, true);
		RealBall player2 = new RealBall(ballTModel, new Vector3f(200.5f, 1, 400), 0, 0, 0, 1, false);
		List<RealBall> balls = new ArrayList<RealBall>();
		balls.add(player1);
		balls.add(player2);

		Camera camera = new Camera(player1);
		World world = new World(camera);
		world.add(new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("metal2"))/*, "arena"/*, "heightmap"*/));
		world.add(new Terrain(0, 1, loader, new ModelTexture(loader.loadTexture("metal2"))/*, "arena"*/, "heightmap"));
		world.getTerrains().get(1).printHeightsToFile("heights");
		System.out.printf("\nmaxHeight for 2nd terrain: %f, minHeight: %f\n\n", world.getTerrains().get(1).getMaxHeight(), world.getTerrains().get(1).getMinHeight());
		world.add(new Terrain(0, 2, loader, new ModelTexture(loader.loadTexture("metal2"))/*, "arena"/*, "heightmap"*/));
		world.add(new Terrain(0, 3, loader, new ModelTexture(loader.loadTexture("metal2"))/*, "arena"/*, "heightmap"*/));

		List<Entity> nature = new ArrayList<Entity>();
		nature.add(new Entity(boxTModel, box, new Vector3f(200, 0, 500), 0, 0, 0, 5, "box"));
		nature.add(new Entity(dragonTModel, dragon_low, new Vector3f(400, 0, 200), 0, 0, 0, 5, "dragon"));
		nature.add(new Entity(treeTModel, tree, new Vector3f(300, 0, 100), 0, 0, 0, 3, "tree"));
		nature.add(f);
		nature.add(g);
		nature.add(h);
		nature.add(i);

		//BoundingBox bbox = nature.get(1).getCollisionData().getBoundingBox();
		//bbox.print();

		MasterRenderer renderer = new MasterRenderer(loader);

		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(loader.loadTexture("fernAtlas"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		//guis.add(gui);

		GuiRenderer guiRenderer = new GuiRenderer(loader);

		world.addEntities(nature);
		world.addLights(lights);

		PhysicsEngine mainEngine = new PhysicsEngine(balls, world);

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), world);

		WaterFrameBuffers fbos = new WaterFrameBuffers();

		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		//waters.add(new WaterTile(75, 120, 0));



		long ago = System.currentTimeMillis();

		//ShotData sData = mainEngine.performVirtualShot(player1, new Vector3f(0, 0, 300));
		//System.out.printf("Shot ends up at: (%f|%f|%f)\n", sData.getEndPosition().x, sData.getEndPosition().y, sData.getEndPosition().z);

		long notSoLongAgo = System.currentTimeMillis();
		System.out.println("Time for virtual shot: " + (notSoLongAgo - ago));




		long after = System.currentTimeMillis();
		System.out.println("\nTIME TO PREPARE MODES ETC.: " + (after - before) + "\n");
		DisplayManager.resetFrameTime();
		while(!Display.isCloseRequested()) {
			world.start();
			System.out.println("\n---- While loop run " + (counter++) + " times ----");
			picker.update();

			if (mainEngine.isEnabled())
				mainEngine.tick();

			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			if (terrainPoint != null){
				//nature.get(0).setPosition(terrainPoint);
			}

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

			/*fbos.bindReflectionFrameBuffer();
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


			fbos.unbindCurrentFrameBuffer();*/
			renderer.processEntity(player1);
			renderer.processEntity(player2);
			renderer.processWorld(world, new Vector4f(0, -1, 0, 10000));
			waterRenderer.render(waters, camera);
			guiRenderer.render(guis);

			DisplayManager.updateDisplay();
			System.out.printf("CURRENT BALL VELOCITY: (%f|%f|%f)\n", player1.getVelocity().x, player1.getVelocity().y, player1.getVelocity().z);
			if (player1.getPosition().y < world.getHeightOfTerrain(player1.getPosition().x, player1.getPosition().z)) {
				System.out.println("\nBALL SINKS INTO GROUND.");
				//break;
			}
			System.out.println("---- While loop end ----\n");
		}

		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

	public static int getCounter() {
		return counter;
	}

}
