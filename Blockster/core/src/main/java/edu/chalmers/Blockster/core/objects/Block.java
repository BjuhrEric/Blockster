package edu.chalmers.Blockster.core.objects;

import java.util.HashSet;
import java.util.Set;

import edu.chalmers.Blockster.core.util.AnimationState;

public class Block extends BlocksterObject {

	private Set<String> properties;
	
	public Block(float startX, float startY, BlockMap blockLayer) {
		super(startX, startY, blockLayer);
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
	public int hashCode() {
		int x = (int) (11 * getX());
		int y = (int) (23 * getY());
		
		return x*x*x*y;
	}
	
	@Override
	public void moveToNextPosition() {
		blockMap.setBlock((int) getOriginX(), (int) getOriginY(), null);
		System.out.println("Removing "+this);
		setX(getOriginX() + anim.getMovement().getDirection().deltaX);
		setY(getOriginY() + anim.getMovement().getDirection().deltaY);
		System.out.println("Moved "+this);
	}
	
	public void setAnimationState(AnimationState anim) {
		super.setAnimationState(anim);
		if (anim != AnimationState.NONE)
			blockMap.addActiveBlock(this);
	}
	
	@Override
	public String toString() {
		return ("Block: (" + getOriginX() + ", " + getOriginY() + ")");
	}
	
}
