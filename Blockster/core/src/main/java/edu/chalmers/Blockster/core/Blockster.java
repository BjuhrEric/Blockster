package edu.chalmers.Blockster.core;

import java.io.File;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Blockster extends Game implements ApplicationListener, StageListener {
	
	//Constant useful for logging.
	public static final String LOG = Blockster.class.getSimpleName();

	//A libgdx helper class that logs current FPS each second
	private FPSLogger fpsLogger;
	private StageController controller;
	private StageView viewer;
	private Stage stage;
	private TiledMap map;
	
	@Override
	public void create () {
		Gdx.app.log(Blockster.LOG, "Creating game");

		StageController controller = new StageController();
		
		/**
		 *  **Temporary** Loading the tmx-file and create a map
		 */
		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load("maps/Block-floor.tmx");
		stage = new Stage(map);
		
		controller.setStage(stage);
		
		viewer = new StageView(controller);
		viewer.init(map);
	}

	@Override
	public void resize (int width, int height) {
		Gdx.app.log(Blockster.LOG, "Resizing game to: " + width + " x " + height);
		
		/**
		 * set the camera view according to the new size
		 */
		viewer.resize(width, height);
		}

	@Override
	public void render () {
		/*Update the world controller with the time
			elapsed between the last two frames. */ 
	
		//controller.update(Gdx.graphics.getDeltaTime());
		
		/* Update the model with the time elapsed between
		 * the last two frames.
		 */
		//stage.update(Gdx.graphics.getDeltaTime());
		
		/* Clear screen */
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		/* Render the new frame */
		viewer.render();
		
	}

	@Override
	public void pause () {
		Gdx.app.log(Blockster.LOG, "Pausing game");
	}

	@Override
	public void resume () {
		Gdx.app.log(Blockster.LOG, "Resuming game");
	}

	@Override
	public void dispose () {
		Gdx.app.log(Blockster.LOG,  "Disposing game");
		viewer.dispose();
	
	}
	
	@Override
	public void stageChanged(Stage stage) {
		this.stage = stage;
	}
}
