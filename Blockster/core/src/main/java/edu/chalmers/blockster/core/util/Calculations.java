package edu.chalmers.blockster.core.util;

public final class Calculations {
	
	public static final float STANDARD_MOVE_DURATION = 0.2f;
	public static final float MOVE_LIFTED_BLOCK_DURATION = 0.15f;
	public static final float BLOCK_FALL_DURATION = 0.05f;
	
	private Calculations() {
		
	}
	
	public static boolean collisionEitherCorner(PhysicalObject player, GridMap blockLayer) {
		try {
			return collisionUpperLeft(player, blockLayer) ||
					collisionUpperRight(player, blockLayer) ||
					collisionLowerLeft(player, blockLayer) ||
					collisionLowerRight(player, blockLayer);
		} catch (NullPointerException e) {
			return false;
		}
	}

	private static boolean collisionLowerLeft(PhysicalObject player, 
			GridMap blockLayer) {
		final float tileWidth = blockLayer.getBlockWidth();
		final float tileHeight = blockLayer.getBlockHeight();
		
		return isSolid(blockLayer, (int) (player.getX() / tileWidth),
				(int) (player.getY() / tileHeight));
	}

	private static boolean collisionLowerRight(PhysicalObject player, 
			GridMap blockLayer) {
		final float tileWidth = blockLayer.getBlockWidth();
		final float tileHeight = blockLayer.getBlockHeight();
		
		return isSolid(blockLayer, (int) ((player.getX() + player.getWidth()) / tileWidth),
				(int) (player.getY() / tileHeight));
	}
	
	private static boolean collisionUpperLeft(PhysicalObject player, 
			GridMap blockLayer) {
		final float tileWidth = blockLayer.getBlockWidth();
		final float tileHeight = blockLayer.getBlockHeight();
		
		return isSolid(blockLayer, (int) (player.getX() / tileWidth),
				(int) ((player.getY() + player.getHeight()) / tileHeight));
	}
	
	private static boolean collisionUpperRight(PhysicalObject player, 
			GridMap blockLayer) {
		final float tileWidth = blockLayer.getBlockWidth();
		final float tileHeight = blockLayer.getBlockHeight();
		
		return isSolid(blockLayer, (int) ((player.getX() + player.getWidth()) / tileWidth),
				(int) ((player.getY() + player.getHeight()) / tileHeight));
	}
	
	private static boolean isSolid(GridMap blockLayer, int x, int y) {
		return blockLayer.hasBlock(x, y) && blockLayer.getBlock(x,y).isSolid();
	}
}
