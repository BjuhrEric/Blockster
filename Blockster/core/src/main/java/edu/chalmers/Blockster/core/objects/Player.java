package edu.chalmers.Blockster.core.objects;

import javax.vecmath.*;

import edu.chalmers.Blockster.core.interactions.BlockGrabbedInteraction;
import edu.chalmers.Blockster.core.interactions.BlockLiftedInteraction;
import edu.chalmers.Blockster.core.interactions.PlayerInteraction;
import edu.chalmers.Blockster.core.util.AnimationState;
import edu.chalmers.Blockster.core.util.Calculations;
import edu.chalmers.Blockster.core.util.Direction;
import edu.chalmers.Blockster.core.util.Movement;
import static edu.chalmers.Blockster.core.util.Calculations.*;

/**
 * The model representing a player in the game Blockster
 * @author Emilia Nilsson and Eric Bjuhr
 *
 */
public class Player extends BlocksterObject{
	
	
	private Vector2f velocity;
	private Vector2f defaultVelocity = new Vector2f(700, 700);
	private float totalTime = 0;
	private Block processedBlock;
	private boolean isGrabbingBlock;
	private boolean isLiftingBlock;
	private PlayerInteraction interaction = PlayerInteraction.NONE;
	
	private boolean collidedHorisontally = false;
	private boolean collidedVertically = false;
	
	public Player(float startX, float startY, BlockMap blockLayer) {
		super(startX, startY, blockLayer);
		velocity = new Vector2f(0, 0);
	}
	
	public Vector2f getVelocity() {
		return velocity;
	}
	
	public void setVelocityX(float velocityX) {
		if(Math.abs(velocityX) > defaultVelocity.x) {
			velocity.x = Math.signum(velocityX) * defaultVelocity.x;
		} else {
			velocity.x = velocityX;
		}
	}
	
	public void setVelocityY(float velocityY) {
		if(Math.abs(velocityY) > defaultVelocity.y) {
			velocity.y = Math.signum(velocityY) * defaultVelocity.y;
		} else {
			velocity.y = velocityY;
		}
	}
	
	public void setDefaultVelocity(Direction dir) {
		setVelocityX(getVelocity().x + dir.deltaX * defaultVelocity.x);
		setVelocityY(getVelocity().y + dir.deltaY * defaultVelocity.y);
	}
	
	public void increaseGravity(float deltaTime) {
		totalTime += deltaTime;
		setVelocityY(-9.82F * totalTime * getBlockLayer().getBlockHeight());
	}
	
	public void resetGravity() {
		totalTime = 0;
	}
	
	public Block getProcessedBlock() {
		return processedBlock;
	}
	
	public void grabBlock(Block block) {
		if (canGrabBlock(block)) {
			System.out.println("Can grab block at " + block.getX() + " " + block.getY());
			processedBlock = block;
			isGrabbingBlock = true;
			interaction = new BlockGrabbedInteraction(this, block, blockMap);
		}
	}
	
	private boolean canGrabBlock(Block block) {
		if(block != null) {
			System.out.print("block != null = " + (block != null) + "|");
			System.out.print("!isInteracting() = " + !isInteracting() + "|");
			System.out.println("isNextToBlock = " + isNextToBlock(block) + "|");
			System.out.println("isMovable || isLiftable = " + (block.isMovable() || block.isLiftable()));
		}
		return block != null && !isLiftingBlock() && !isInteracting() && isNextToBlock(block) &&
				(block.isMovable() || block.isLiftable());
	}
	
	public boolean isGrabbingBlock() {
		return isGrabbingBlock;
	}
	
	public void liftBlock() {
		if (canLiftBlock(getProcessedBlock())) {
			System.out.println("Can lift block at " + getProcessedBlock().getX() + " " + getProcessedBlock().getY());
			//Lift process			
			isLiftingBlock = true;
			interaction = new BlockLiftedInteraction(this, getProcessedBlock(), blockMap);
			isGrabbingBlock = false;
		}
	}
	
	private boolean canLiftBlock(Block block) {
		return block != null && !isInteracting() &&
				isNextToBlock(block) && block.isLiftable();
	}
	
	public boolean isLiftingBlock() {
		return isLiftingBlock;
	}
	
	public Block getAdjacentBlock(Direction dir) {
		return Calculations.getAdjacentBlock(dir, this, getBlockLayer());
	}
	
	public boolean isNextToBlock(Block block) {
		if(block != null) {
		System.out.println("X: " + Math.abs(block.getX() - (getX() / blockMap.getBlockWidth())) + " Y: " + 
				Math.abs(block.getY() - (getY() / blockMap.getBlockHeight())));
		}
		return block != null &&
		Math.abs(block.getX() - (getX() / blockMap.getBlockWidth())) < 1f &&
		Math.abs(block.getY() - (getY() / blockMap.getBlockHeight())) < 1f;
	}
	
	public void interact(Direction dir) {
		if (isInteracting()) {
			interaction.interact(dir);
		} else {
			setDefaultVelocity(dir);
		}
	}
	
	public boolean isInteracting() {
		return (interaction != PlayerInteraction.NONE);
	}
	
	public void endInteraction() {
		interaction.endInteraction();
		interaction = PlayerInteraction.NONE;
	}
	
	public void move(Vector2f distance) {
		float[] previousPosition = { getX(), getY() };
		collidedHorisontally = false;
		collidedVertically = false;

		setX(getX() + distance.x);
		if (collisionEitherCorner(this, getBlockLayer())) {
			setX(previousPosition[0]);
			collidedHorisontally = true;
		}

		setY(getY() + distance.y);
		if (collisionEitherCorner(this, getBlockLayer())) {
			setY(previousPosition[1]);
			if (distance.y < 0) {
				setY(((int)getY()/getBlockLayer().getBlockHeight())
							* getBlockLayer().getBlockHeight());
			}
			collidedVertically = true;
		}
	}
	
	public void moveToNextPosition() {
		super.moveToNextPosition();
	}

	public void updatePosition(float deltaTime) {
		if (getAnimationState() != AnimationState.NONE) {
			getAnimationState().updatePosition(deltaTime);
		} else {
			Vector2f distance = new Vector2f();
			distance.x = velocity.x * deltaTime;
			distance.y = velocity.y * deltaTime;
			move(distance);
		}
	}
	
	/**
	 * This method is used when pulling a block and checks if the player
	 * can continue to pull it or if there is something blocking the way
	 * (this is usually taken care of by the collision avoidance, but when
	 * moving a block then this isn't available).
	 * 
	 * @param movement
	 * @return true if nothing is blocking the way behind player, else false.
	 */
	public boolean canMove(Movement movement) {
		BlockMap bLayer = getBlockLayer();
		int checkX = (int) (getX() / bLayer.getBlockWidth());
		int checkY = (int) (getY() / bLayer.getBlockWidth());
			
		return checkX >= 1 && checkX < bLayer.getWidth() && !bLayer.
				hasBlock(checkX + movement.getDirection().deltaX, checkY);
			
	}
	
	public boolean collidedHorisontally() {
		return collidedHorisontally;
	}
	
	public boolean collidedVertically() {
		return collidedVertically;
	}

	public void setGrabbing(boolean b) {
		if(b) {
			float relativePositionSignum = getProcessedBlock().getX()
					- getX() / blockMap.getBlockWidth();
			AnimationState anim = relativePositionSignum > 0 ?
			AnimationState.GRAB_RIGHT :
			AnimationState.GRAB_LEFT;
			setAnimationState(anim);
		} else {
			setAnimationState(AnimationState.NONE);
		}
		
	}
	
}