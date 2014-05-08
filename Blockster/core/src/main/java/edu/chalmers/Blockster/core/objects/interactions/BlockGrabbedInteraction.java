package edu.chalmers.Blockster.core.objects.interactions;

import java.util.ArrayList;
import java.util.List;

import edu.chalmers.Blockster.core.objects.Block;
import edu.chalmers.Blockster.core.objects.BlockMap;
import edu.chalmers.Blockster.core.objects.Player;
import edu.chalmers.Blockster.core.objects.movement.AnimationState;
import edu.chalmers.Blockster.core.objects.movement.Direction;
import edu.chalmers.Blockster.core.objects.movement.Movement;

public class BlockGrabbedInteraction extends PlayerInteraction {
	
	private Block activeBlock; 
	private BlockMap blockLayer;
	private Player player;
	
	public BlockGrabbedInteraction(Player player, 
			Block activeBlock, BlockMap blockLayer) {
		this.activeBlock = activeBlock;
		this.blockLayer = blockLayer;
		this.player = player;
	}
	
	@Override
	public void interact(Direction dir) {
		
		System.out.println("Interacting: " + dir.name());
		float relativePosition = activeBlock.getX() 
				- player.getX() / blockLayer.getBlockWidth();
		Movement movement = Movement.getPushPullMovement(dir, relativePosition);
		List<Block> moveableBlocks;
		

		System.out.println((movement.isPullMovement() ? "IS" : "ISN'T")+ " PULL MOVEMENT ("+movement.name()+")");
		if (movement.isPullMovement()) {
			if (player.canMove(dir)) {
				System.out.println("Can pull");
				activeBlock.setAnimationState(new AnimationState(movement));
				player.setAnimationState(new AnimationState(movement));
				blockLayer.setBlock((int) activeBlock.getX(), (int) activeBlock.getY(), null);
			}
		} else {
			moveableBlocks = getMoveableBlocks(dir);
			for (Block block : moveableBlocks) {
				block.setAnimationState(new AnimationState(movement));
				blockLayer.setBlock((int) block.getX(), (int) block.getY(), null);
			}
			
			if (!moveableBlocks.isEmpty()) {
				System.out.println("Can push");
				player.setAnimationState(new AnimationState(movement));
			}
		}

		
	}
	
	public List<Block> getMoveableBlocks(Direction dir) {
		/* Create a list to put the block to be moved in. */
		List<Block> movingBlocks = new ArrayList<Block>();
		
		/* Origin of add process */
		int origX = (int) activeBlock.getX();
		int origY = (int) activeBlock.getY();
		
		if (origY >= blockLayer.getHeight()) {
			return movingBlocks;
		}

		/* We've already established that the move is okay,
			so we don't need to change the Y coordinate. */
		int checkX = (origX);

		/* Loop to add all the blocks to be moved to the list. */
		while (blockLayer.hasBlock(checkX, origY) && checkX > 0 
				&& checkX < blockLayer.getWidth()) {
			if(!blockLayer.hasBlock(checkX, origY + 1) && blockLayer.
					getBlock(checkX, origY).isMovable()) {
				movingBlocks.add(blockLayer.getBlock(checkX, origY));
				checkX += dir.deltaX;
			} else {
				movingBlocks.clear();
				break;
			}
		}
		
		return movingBlocks;
	}

	@Override
	public void endInteraction() {
		player.setGrabbing(false);
	}

	@Override
	public void startInteraction() {
		player.setGrabbing(true);
	}
	


}