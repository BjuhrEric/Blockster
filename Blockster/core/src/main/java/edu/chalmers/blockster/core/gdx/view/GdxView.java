package edu.chalmers.blockster.core.gdx.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import edu.chalmers.blockster.core.Model;
import edu.chalmers.blockster.core.objects.Block;
import edu.chalmers.blockster.core.objects.Player;
import edu.chalmers.blockster.core.objects.movement.AnimationFactory;

/**
 * @author Joel Tegman, Oskar Jönefors
 */
public class GdxView implements ApplicationListener, Disposable {

	private OrthographicCamera camera;
	private final Model model;
	private OrthogonalTiledMapRenderer renderer;
	private Stage stage;
	private Map<Player, PlayerView> players;
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
	@Override
	public void render(){

		/* Checks if the camera should transit between the players */
		if (model.isSwitchChar) {
			transitCamera();
		}

		/* Follow the active player */
		final Player activePlayer = model.getActivePlayer();
		final float activePlayerX = activePlayer.getX();
		final float activePlayerY = activePlayer.getY();
		
		camera.position.set(activePlayerX, activePlayerY, 0);

		/* Move the background with the player */
		background.setPosition(
				activePlayerX * 0.7f - 
						background.getScaleX() * background.getWidth() - 
						camera.viewportWidth / 2,
						activePlayerY * 0.7f - 
								background.getHeight() / 2 - 
								camera.viewportHeight / 2);
		

		/**
		 *  renders the stage
		 */
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		drawObjects();
		
		renderer.setView(camera);
		renderer.render();
	}

	/**
	 * Initialize the view.
	 */
	public void init() {
		players = new HashMap<Player, PlayerView>();
		for (final Player player : model.getPlayers()) {
			players.put(player, createPlayerView(player));
		}
		
		
		//BlockLayer layer = model.getMap().getBlockLayer();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		camera = new OrthographicCamera();
		renderer = new OrthogonalTiledMapRenderer((GdxMap) model.getMap());
		stage.setCamera(camera);
		

		/* Add the background */
		final Texture tex = new Texture("maps/background-11.jpg");
		background = new GdxBackgroundActor(new TextureRegion(tex));
		background.setScale(5);
		stage.addActor(background);
	}
	
	public void transitCamera(){
		Vector3 cameraMoveVector = new Vector3(model.getActivePlayer().getX(), model.getActivePlayer().getY(), 0);

		/* Set the "direction" of the cameraMoveVector towards the active player */
		cameraMoveVector.sub(camera.position);

		/* Set the vector to a proper size. 
		 * This will decide how fast the camera moves */
		cameraMoveVector.nor();
		cameraMoveVector.mul(50f);

		camera.translate(cameraMoveVector);

		background.setPosition(
				camera.position.x*0.7f - 
						background.getScaleX()*background.getWidth() - 
						camera.viewportWidth / 2,
						camera.position.y*0.7f - 
								(background.getHeight() / 2) - 
								camera.viewportHeight / 2);


		final boolean cameraInPlace = camera.position.epsilonEquals(model.getActivePlayer().getX(), model.getActivePlayer().getY(), 0, 30f);

		if (cameraInPlace) {
			model.isSwitchChar = false;
		}
	}

	
	public void refreshRenderer() {
		renderer.setMap((GdxMap) model.getMap());
	}
	
	public void refreshStage() {
		stage.clear();
		stage.setCamera(camera);
		stage.addActor(background);
	}
	
	
	@Override
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
	
	public void drawPlayers(SpriteBatch batch) {

		
		for (final Player player : model.getPlayers()) {
			PlayerView pView = players.get(player);
			if (pView == null) {
				players.put(player, pView = createPlayerView(player));
			}
			pView.draw(batch);
		}
		
		
	}
	
	public void drawBlocks(SpriteBatch batch) {
		final GdxMap map = (GdxMap)model.getMap();
		
		for (final Block block : map.getActiveBlocks()) {
			final BlockView bView = map.getBlockView(block);
			if (bView != null) {
				bView.draw(batch);
			}
		}
	}
	
	public void drawObjects() {
		camera.update();
		final SpriteBatch batch = stage.getSpriteBatch();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		drawPlayers(batch);
		drawBlocks(batch);
		
		batch.end();
	}
 	
	public PlayerView createPlayerView(Player player) {

		final Texture tex = new Texture("Player/still2.png");
		final TextureRegion texr = new TextureRegion(tex);
		final AnimationFactory animF = new AnimationFactory();
		System.out.println("hit kom");
		return new PlayerView(player, animF.getArrayOfAnimations(),
				animF.getWalkAnimations(), texr);
	}
}
