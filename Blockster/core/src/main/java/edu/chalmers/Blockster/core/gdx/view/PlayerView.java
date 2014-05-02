package edu.chalmers.Blockster.core.gdx.view;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import edu.chalmers.Blockster.core.AnimationState;
import edu.chalmers.Blockster.core.Movement;
import edu.chalmers.Blockster.core.Player;

public class PlayerView extends Actor {
	private Player player;
	private Sprite sprite;
	private TextureRegion defaultSprite;
	private HashMap<Movement, Animation> arrayOfAnimation;
	
	public PlayerView(Player player, HashMap<Movement, Animation> hashMap, 
			TextureRegion texture) {
		this.player = player;
		arrayOfAnimation = hashMap;
		defaultSprite = texture; 
		
		sprite = new Sprite();
		setWidth(defaultSprite.getRegionWidth());
		setHeight(defaultSprite.getRegionHeight());
	}
	
	public void draw(SpriteBatch batch, float alpha){
		sprite.setRegion(chooseAnimation());
		batch.draw(sprite, getX(), getY());
	}
			
		
	public TextureRegion chooseAnimation(){
		AnimationState animState = player.getAnimationState();
		Movement move = animState.getMovement();
		if (move == Movement.NONE) {
			return defaultSprite;
		} else {
			return getCurrentAnimation(move, animState.getElapsedTime());
		}
	}
	
	public TextureRegion getCurrentAnimation(Movement move, Float time){
		return arrayOfAnimation.get(move).getKeyFrame(time, true);
	}
	
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		player.setHeight(height);
	}
	
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		player.setWidth(width);
	}
	
	public float getX() {
		return player.getX();
	}
	
	public float getY() {
		return player.getY();
	}

}
