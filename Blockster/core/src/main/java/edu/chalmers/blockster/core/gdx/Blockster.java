package edu.chalmers.blockster.core.gdx;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import edu.chalmers.blockster.core.MapChangeListener;
import edu.chalmers.blockster.core.Model;
import edu.chalmers.blockster.core.gdx.controller.Controller;
import edu.chalmers.blockster.core.gdx.view.GdxFactory;
import edu.chalmers.blockster.core.gdx.view.GdxView;
/**
 * 
 * @author Oskar Jönefors, Eric Bjuhr
 *
 */
public class Blockster extends Game implements MapChangeListener {
	
	//Constant useful for logging.
	public static final String LOG = Blockster.class.getSimpleName();

	private Controller controller;
	private GdxView viewer;
	private Model stage;
	private Map<Model, GdxView> stages;
	
	private void addStagesToMap(Map<Model, GdxView> stageMap, File... maps) {
		final TmxMapLoader loader = new TmxMapLoader();
		for (final File mapFile : maps) {
			Gdx.app.log(Blockster.LOG, "Stage found: "+mapFile.getName());
			final TiledMap map = loader.load("maps/"+mapFile.getName());
			final GdxFactory factory = new GdxFactory(map);
			final Model model = new Model(factory, mapFile.getName());
			final GdxView view = new GdxView(model, factory);
			view.init();
			
			stageMap.put(model, view);
		}
	}
	
	@Override
	public void create () {
		Gdx.app.log(Blockster.LOG, "Creating game");
		controller = new Controller();
		controller.addMapChangeListener(this);
		try {
			/*
			FileHandle fh = new FileHandle(new File(new File("assets"), "Gourmet Race.mp3"));
			Music music = Gdx.audio.newMusic(fh);
			music.play();
			music.setLooping(true);
			*/
			
			Gdx.app.log(Blockster.LOG, "Loading stages");
			loadStages();

			Gdx.app.log(Blockster.LOG, "Setting stage");
			
			final Iterator<Model> modelIterator = stages.keySet().iterator();
			//modelIterator.next();
			final Model model = modelIterator.next();
			
			controller.setModel(model);
		} catch (SecurityException | IOException e) {
			Gdx.app.log(Blockster.LOG, e.getClass().getName());
		}
	}
	
	@Override
	public void dispose () {
		Gdx.app.log(Blockster.LOG,  "Disposing game");
		viewer.dispose();
	
	}
	
	private File[] listFilesInDirectory(File directory, final String fileEnding) {
		
		final FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(fileEnding);
			}
		};
		
		return directory.listFiles(ff);
	}

	private void loadStages() throws IOException {
		stages = Collections.synchronizedMap(new TreeMap<Model, GdxView>());
		addStagesToMap(stages, listFilesInDirectory(new File("assets/maps/"), ".tmx"));
	}

	@Override
	public void pause () {
		Gdx.app.log(Blockster.LOG, "Pausing game");
	}

	@Override
	public void render () {
		/*Update the world controller with the time
			elapsed between the last two frames. */ 
		controller.update();
		
		/* Update the model with the time elapsed between
		 * the last two frames.
		 */
		stage.update(Gdx.graphics.getDeltaTime());
		
		/* Clear screen */
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		/* Render the new frame */
		viewer.render();
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
	public void resume () {
		Gdx.app.log(Blockster.LOG, "Resuming game");
	}
	
	@Override
	public void stageChanged(Model stage) {
		this.stage = stage;
		viewer = stages.get(stage);
		controller.setView(viewer);
		viewer.refreshRenderer();
		viewer.refreshStage();
		Gdx.app.log(Blockster.LOG, "Recieved a stage changed event");
	}
}
