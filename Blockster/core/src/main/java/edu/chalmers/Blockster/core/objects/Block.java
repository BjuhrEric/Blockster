package edu.chalmers.Blockster.core.objects;

import java.util.HashSet;
import java.util.Set;

import edu.chalmers.Blockster.core.objects.movement.AnimationState;
import edu.chalmers.Blockster.core.util.GridObject;

public class Block extends BlocksterObject implements GridObject {

	private Set<String> properties;
	
	public Block(float startX, float startY, BlockMap blockLayer) {
		super(startX, startY, blockLayer, 1, 1);
		properties = new HashSet<String>();
	}
	
	public void setProperty(String property) {
		properties.add(property.toLowerCase());
	}
	
	public boolean isSolid() {
		return properties.contains("solid");
	}
	
	public boolean isLiftable() {
		return properties.contains("liftable");
	}
	
	public boolean isMovable() {
		return properties.contains("movable");
	}
	
	public boolean hasWeight() {
		return properties.contains("weight");
	}
	
	@Override
	public void moveToNextPosition() {
		blockMap.setBlock((int) getOriginX(), (int) getOriginY(), null);
		System.out.println("Removing "+this);
		super.moveToNextPosition();
		
	}
	
	public void setAnimationState(AnimationState anim) {
		super.setAnimationState(anim);
		if (anim != AnimationState.NONE)
			blockMap.addActiveBlock(this);
	}
	
}
