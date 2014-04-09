package edu.chalmers.Blockster.core.gdx.view;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import edu.chalmers.Blockster.core.Block;
import edu.chalmers.Blockster.core.Animation;
import edu.chalmers.Blockster.core.Model;
import edu.chalmers.Blockster.core.Player;
import edu.chalmers.Blockster.core.util.Direction;

/**
 * @author Joel Tegman, Oskar Jönefors
 */
public class GdxView implements ApplicationListener, Disposable {

	private OrthographicCamera camera;
	private Model model;
	private OrthogonalTiledMapRenderer renderer;
	private Stage stage;
	private List<GdxBlock> activeBlocks;
	private List<GdxBlock> liftedBlocks;
	private Actor background;
	
	public GdxView(Model model) {
		this.model = model;
	}
	
	@Override
	public void dispose() {
		stage.dispose();
		renderer.dispose();
	}
	
	/**
	 * Render the view.
	 */
	public void render(){
		
		/* Follow the active player */
		camera.position.set(model.getActivePlayer().getX(),
				model.getActivePlayer().getY(), 0);
		
		/* Move the background with the player */
		background.setPosition(
								(model.getActivePlayer().getX()*0.7f - 
				background.getScaleX()*background.getWidth() - 
				camera.viewportWidth / 2),
								(model.getActivePlayer().getY()*0.7f - 
				(background.getHeight() / 2) - 
				camera.viewportHeight / 2));
		
		for (Block block : model.getActiveBlocks()) {
			if (!activeBlocks.contains(block) || !((GdxBlock)block).hasParent()) {
				GdxBlock gBlock = (GdxBlock)block;
				Animation anim = block.getAnimation();
				Direction dir = anim.direction;
				float duration = anim.duration;
				
				activeBlocks.add(gBlock);
				stage.addActor(gBlock);
				gBlock.setOrigin(gBlock.getX(), gBlock.getY());
				gBlock.addAction(new MoveBlockAction(dir, duration, model.getMap(), model));
				((GdxBlockLayer) model.getMap().getBlockLayer()).removeBlock(block);
				Gdx.app.log("GdxView", "Added actor. Coordinates:" + gBlock.getX() + " " + gBlock.getY());
			}
		}
		
		for (Player player : model.getPlayers()) {
			
			if (!stage.getActors().contains((GdxPlayer) player, true)) {
				stage.addActor((GdxPlayer) player);
				Gdx.app.log("GdxView", "Added actor.");
			}
		}
		
		/**
		 *  renders the stage
		 */
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();		
		renderer.setView(camera);
		renderer.render();
	}
	
	/**
	 * Initialize the view.
	 */
	public void init() {
		//BlockLayer layer = model.getMap().getBlockLayer();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		camera = new OrthographicCamera();
		renderer = new OrthogonalTiledMapRenderer((GdxMap) model.getMap());
		stage.setCamera(camera);

		/* Add the background */
		Texture tex = new Texture("maps/background-11.jpg");
		background = new GdxBackgroundActor(new TextureRegion(tex));
		background.setScale(5);
		stage.addActor(background);
		

		
		activeBlocks = new ArrayList<GdxBlock>();
		liftedBlocks = new ArrayList<GdxBlock>();
	}
	
	public void refreshRenderer() {
		renderer.setMap((GdxMap) model.getMap());
	}
	
	public void refreshStage() {
		stage.clear();
		stage.setCamera(camera);
		stage.addActor(background);
	}
	
	
	public void resize(int width, int height){
		camera.viewportHeight = height*5;
		camera.viewportWidth = width*5;
		camera.update();
	}

	@Override
	public void create() {
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
