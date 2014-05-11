package edu.chalmers.blockster.core.objects;

import javax.vecmath.*;
import edu.chalmers.blockster.core.objects.interactions.BlockGrabbedInteraction;
import edu.chalmers.blockster.core.objects.interactions.BlockLiftedInteraction;
import edu.chalmers.blockster.core.objects.interactions.Interactor;
import edu.chalmers.blockster.core.objects.interactions.PlayerInteraction;
import edu.chalmers.blockster.core.objects.movement.AnimationState;
import edu.chalmers.blockster.core.objects.movement.Direction;
import edu.chalmers.blockster.core.objects.movement.Movement;
import static edu.chalmers.blockster.core.util.Calculations.*;

/**
 * The model representing a player in the game Blockster
 * @author Emilia Nilsson and Eric Bjuhr
 *
 */
public class Player extends BlocksterObject implements Interactor {
	

	private boolean dancing = false;
	
	private Block processedBlock;
	private boolean grabbingBlock;
	private boolean liftingBlock;
	private PlayerInteraction interaction = PlayerInteraction.NONE;
	private boolean horizontalCollision;
	private boolean verticalCollision;
	private int wait = 0;
	
	public Player(float startX, float startY, BlockMap blockLayer) {
		super(startX, startY, blockLayer, blockLayer.getBlockWidth(), blockLayer.getBlockHeight());
		defaultVelocity = new Vector2f(8 * blockMap.getBlockWidth(), 
				55 * blockMap.getBlockHeight());
	}

	private boolean canClimbBlock(Block block) {
		if (block == null) {
			return false;
		}
		final int blockX = (int)block.getX();
		final int blockY = (int)block.getY();
		final int playerGridX = (int)(getX() / getScaleX());
		final int playerGridY = (int)(getY() / getScaleY());
		
		return block != null && !isGrabbingBlock() && !isLiftingBlock() &&
				isNextToBlock(block) && !blockMap.hasBlock(blockX, blockY + 1) &&
				!blockMap.hasBlock(playerGridX, playerGridY + 1);
	}
	
	private boolean canGrabBlock(Block block) {
		return block != null && !isLiftingBlock() && !isBusy() && isNextToBlock(block) &&
				(block.isMovable() || block.isLiftable());
	}
	
	private boolean canLiftBlock(Block block) {
		return block != null && grabbingBlock &&
				isNextToBlock(block) && block.isLiftable();
	}
	
	public boolean canMove(Direction dir) {
		final BlockMap bLayer = getBlockLayer();
		final float blockWidth = bLayer.getBlockWidth();
		final int checkX = (int) (getOriginX() / getScaleX());
		final int checkY = (int) (getOriginY() / getScaleY());
		
		return checkX >= 1 && checkX < blockWidth && !bLayer.
				hasBlock(checkX + dir.deltaX, checkY + dir.deltaY)
				&& bLayer.hasBlock(checkX + dir.deltaX, checkY + dir.deltaY - 1);
			
	}
	
	public void climbBlock() {
		final Block block = getAdjacentBlock();
		System.out.println("Can we climb block?");
		if(canClimbBlock(block)) {
			System.out.println("We can climb block!");
			Direction dir = Direction.getDirection(getX() / blockMap.getBlockWidth(), block.getX());
			AnimationState climb = new AnimationState(Movement.getClimbMovement(dir));
			setAnimationState(climb);
		}
	}
	
	public boolean collidedHorisontally() {
		return horizontalCollision;
	}
	
	public boolean collidedVertically() {
		return verticalCollision;
	}
	
	public void endInteraction() {
		interaction.endInteraction();
		System.out.println("ENDING INTERACTION");
	}
	
	public Block getProcessedBlock() {
		return processedBlock;
	}
	
	public void grabBlock() {
		final Block block = getAdjacentBlock();
		if (canGrabBlock(block)) {
			System.out.println("Can grab block at " + block.getX() + " " + block.getY());
			processedBlock = block;
			grabbingBlock = true;
			interaction = new BlockGrabbedInteraction(this, block, blockMap);
			interaction.startInteraction();
		}
	}
	
	public void interact() {
		if (isInteracting()) {
			if (getAnimationState().isDone()) {
				if (!directionChanged || !isLiftingBlock()) {
					interaction.interact(getDirection());
					directionChanged = false;
				} else {
					wait++;
					if (wait > 2) {
						directionChanged = false;
						wait = 0;
					}
				}
				
			}
		} else {
			setDefaultVelocity(getDirection());
		}
	}
	
	public boolean isBusy() {
		return isInteracting() || getAnimationState() != AnimationState.NONE;
	}
	
	public boolean isDancing() {
		return dancing;
	}
	
	public boolean isGrabbingBlock() {
		return grabbingBlock;
	}
	
	public boolean isInteracting() {
		return interaction != PlayerInteraction.NONE;
	}
	
	public boolean isLiftingBlock() {
		return liftingBlock;
	}
	
	public boolean isNextToBlock(Block block) {
		return block != null &&
			Math.abs(block.getX() - (Math.round(getX()) / 
					blockMap.getBlockWidth())) <= 1.1f &&
			block.getY() == (getY() / blockMap.getBlockHeight());
	}

	public void liftBlock() {
		Block processedBlock = getProcessedBlock();
		System.out.println("Trying to lift block " + processedBlock);
		if (canLiftBlock(processedBlock)) {
			System.out.println("Can lift block at " + processedBlock.getX() + " " + processedBlock.getY());
			//Lift process			
			interaction = new BlockLiftedInteraction(this, processedBlock, blockMap);
			interaction.startInteraction();
			grabbingBlock = false;
		}
	}
	
	public void move(Vector2f distance) {
		float[] previousPosition = { getX(), getY() };
		
		if (Math.abs(distance.x) > 0 ) {
			setX(getX() + distance.x);
			if (collisionEitherCorner(this, getBlockLayer())) {
				setX(previousPosition[0]);
				horizontalCollision = true;
			} else {
				horizontalCollision = false;
				verticalCollision = false;
			}
		}

		if (Math.abs(distance.y) > 0 ) {
			setY(getY() + distance.y);
			if (collisionEitherCorner(this, getBlockLayer())) {
				setY(previousPosition[1]);
				if (distance.y < 0) {
					setY(((int)getY()/getBlockLayer().getBlockHeight())
								* getBlockLayer().getBlockHeight());
				}
				verticalCollision = true;
			} else {
				verticalCollision = false;
				horizontalCollision = false;
			}
		}
	}
	
	public void setDancing(boolean b) {
		dancing = true;
	}
	
	public void setGrabbing(boolean b) {
		grabbingBlock = b;
		if(b) {
			setAnimationState(getDirection() == Direction.LEFT ? AnimationState.GRAB_LEFT : AnimationState.GRAB_RIGHT);
		} else {
			setAnimationState(AnimationState.NONE);
			interaction = PlayerInteraction.NONE;
			processedBlock = null;
		}
	}

	public void setLifting(boolean lift) {
		liftingBlock = lift;
		if(lift) {
			setAnimationState(getDirection() == Direction.LEFT ?
					AnimationState.LIFT_LEFT : AnimationState.LIFT_RIGHT);
		} else {
			setAnimationState(getDirection() == Direction.LEFT ?
					AnimationState.PLACE_LEFT : AnimationState.PLACE_RIGHT);
			interaction = PlayerInteraction.NONE;
			processedBlock = null;
		}
	}
	
	public void updatePosition(float deltaTime) {
		final AnimationState anim = getAnimationState();
		if (anim == AnimationState.NONE) {
			final Vector2f velocity = getVelocity();
			final Vector2f distance = new Vector2f(velocity.x * deltaTime,
					velocity.y * deltaTime);
			if (Math.abs(Math.hypot(distance.x, distance.y)) > 0) {
				move(distance);
			}
			
		} else {
			System.out.println("Moving "+this);
			anim.updatePosition(deltaTime);
		}
	}
}
