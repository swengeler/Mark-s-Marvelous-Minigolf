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
import programStates.State;
import programStates.DesignerState;
import programStates.GameState;
import programStates.MenuState;
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
	
	private static State currState;
	private static Loader loader = new Loader();
	private static boolean update = false;
	
	private static int counter;
	
	public static void main(String[] args) {
	
		DisplayManager.createDisplay();
		currState = new MenuState(loader);
		
		DisplayManager.reset();
		
		while(!Display.isCloseRequested()){
			if(!update) {

			currState.checkInputs();
			currState.update();
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			currState.renderScreen();
			counter++;
			
			
			DisplayManager.updateDisplay();
			}
		}
		currState.cleanUp();
		DisplayManager.closeDisplay();
		
	}
	
	public static void loadGame(){
		currState.cleanUp();
		DisplayManager.closeDisplay();
		DisplayManager.createDisplay();
		currState = new GameState(loader);
	}
	
	public static void loadGame(World world){
		currState.cleanUp();
		DisplayManager.closeDisplay();
		DisplayManager.createDisplay();
		currState = new GameState(loader, world);
	}
	
	public static void loadMenu(){
		currState.cleanUp();
		DisplayManager.closeDisplay();
		DisplayManager.createDisplay();
		currState = new MenuState(loader);
	}

	
	public static void loadDesigner(){
		currState.cleanUp();
		DisplayManager.closeDisplay();
		DisplayManager.createDisplay();
		currState = new DesignerState(loader);
	}
	
	public static int getCounter() {
		return counter;
	}

}
