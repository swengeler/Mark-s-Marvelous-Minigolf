package programStates;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import guis.GuiButton;
import guis.GuiRenderer;
import guis.GuiTexture;
import particles.ParticleSystem;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import water.WaterFrameBuffers;
import water.WaterRenderer;

public class MenuState implements State {

	private ArrayList<GuiButton> guis;
	private Camera camera;
	private Loader loader;
	
	private MasterRenderer renderer;
	private WaterRenderer waterRenderer;
	private GuiRenderer guiRenderer;
	
	private ArrayList<ParticleSystem> particles = new ArrayList<ParticleSystem>();
	
	private WaterFrameBuffers fbos;
	
	public MenuState(Loader loader){
		init(loader);
	}
	
	private void loadGuis() {
		guiRenderer = new GuiRenderer(loader);
		guis = new ArrayList<GuiButton>();
		GuiButton test = new GuiButton("health", new Vector2f(1000f,800f), loader);
		guis.add(test);
	}

	@Override
	public void renderScreen() {
		guiRenderer.renderButtons(guis);

	}

	@Override
	public void checkInputs() {
		if(Mouse.isButtonDown(0)){
			System.out.println("X=" + Mouse.getX() + ", Y=" + Mouse.getY());
			for(GuiButton button:guis){
				if(button.isInside(new Vector2f(Mouse.getX(), Mouse.getY()))){
					button.click();
				}
			}
		}
	}


	@Override
	public void init(Loader loader) {
		this.loader = loader;
		loadGuis();
	}

	@Override
	public void update() {
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

}
