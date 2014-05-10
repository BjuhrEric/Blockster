package edu.chalmers.blockster.core.objects.movement;

import javax.vecmath.Vector2f;

/**
 * A Spline representing a linear movement.
 * @author Oskar Jönefors
 *
 */
public class LinearSpline implements Spline {
	private final Direction dir;
	
	/**
	 * Create a spline in the given direction.
	 * @param dir
	 */
	public LinearSpline(Direction dir) {
		this.dir = dir;
	}
	
	@Override
	public Vector2f getPosition(float percent) {
		
		if (percent <= 0 || percent > 100) {
			return new Vector2f();
		}
		float offsetX;
		float offsetY;
		
		offsetX = percent * dir.deltaX / 100f;
		offsetY = percent * dir.deltaY / 100f;
		
		return new Vector2f(offsetX, offsetY);
	}

	@Override
	public Direction getDirection() {
		return dir;
	}

}
