package edu.chalmers.blockster.core.gdx.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

import edu.chalmers.blockster.core.objects.Block;
import edu.chalmers.blockster.core.objects.BlockMap;
import edu.chalmers.blockster.core.objects.movement.AnimationState;

/**
 * 
 * @author Mia
 * @author Eric Bjuhr
 *
 */
public class GdxMap extends TiledMap implements BlockMap {

	private final TiledMapTileLayer blockLayer;
	private final Map<Block, BlockView> blocks;
	private final Set<Block> activeBlocks;
	private final float[][] playerStartingPositions;

	public GdxMap (TiledMap map) {
		super();
		playerStartingPositions = getPlayerStartingPositions(map);
		
		blockLayer = (TiledMapTileLayer) map.getLayers().get(0);
		blocks = new HashMap<Block, BlockView>();
		activeBlocks = new HashSet<Block>();

		getLayers().add(blockLayer);

		for (int x = 0; x < blockLayer.getWidth(); x++) {
			for (int y = 0; y < blockLayer.getHeight(); y++) {
				final Cell cell = blockLayer.getCell(x, y);
				if (cell != null) {
					final TiledMapTile tile = cell.getTile();
					final Block block = new Block(x, y, this);
					final BlockView bView = new BlockView(block, tile);
					
					cell.setTile(bView);
					blockLayer.setCell(x, y, cell);
					blocks.put(block, bView);
					
					
					final Iterator<String> properties = tile.getProperties().getKeys();
					while(properties.hasNext()) {
						final String property = properties.next();
						block.setProperty(property);
					}
				}
			}
		}
	}

	private float[][] getPlayerStartingPositions(TiledMap map) {
		
		final MapProperties mapProps = map.getProperties();
		
		if(!mapProps.containsKey("nbrOfPlayers")) {
			throw new IllegalArgumentException("Given map does not contain"
					+ "\"nbrOfPlayers\" property.");
		}
		
		int nbrOfPlayers;
		
		try {
			nbrOfPlayers = Integer.parseInt((String)mapProps.get("nbrOfPlayers"));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Given map's nbrOfPlayers value "
					+ mapProps.get("nbrOfPlayers") + " is incorrect. Should be a number 0 <= 10");
		}
			
		if(nbrOfPlayers < 0 || nbrOfPlayers > 10) {
			throw new IllegalArgumentException("Given nbrOfPlayers value " +
					+ nbrOfPlayers + " is incorrect. Should be a number 0 <= 10");
		}
		
		float[][] startingPositions = new float[nbrOfPlayers][2];
		
		for(int i = 1; i <= nbrOfPlayers ; i++) {
			if(!mapProps.containsKey("playerStart" + i)) {
				throw new IllegalArgumentException("Given map does not contain"
						+ " starting position playerStart" + i + " for player " + i);
			}
			
			final String playerStart = (String)mapProps.get("playerStart" + i);
			final String[] playerStarts = playerStart.split(":");
			float[] playerStartFloats = new float[playerStarts.length];
			
			for(int j = 0; j < playerStarts.length; j++) {
				try {
					playerStartFloats[j] = Float.parseFloat(playerStarts[j]);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Field player start has wrong"
							+ " format: " + playerStart + ". Should be an float position"
									+ " with the following format: x:y");
				}
			}
			startingPositions[i - 1] = playerStartFloats;
		}
		return startingPositions;
	}
	
	public float[][] getPlayerStartingPositions() {
		return playerStartingPositions.clone();
	}
	
	@Override
	public float getBlockWidth(){
		return blockLayer.getTileWidth();
	}

	@Override
	public float getBlockHeight(){
		return blockLayer.getTileHeight();
	}
	
	@Override
	public Block getBlock(int x, int y){
		final Cell cell = blockLayer.getCell(x, y);
		if (cell != null) {
			final BlockView bView = (BlockView) cell.getTile();
			return bView.getBlock();
		}
		return null;
	}
	
	@Override
	public void setBlock(int x, int y, Block block){
		if (block == null) {
			System.out.println("Removing cell at ("+x+", "+y+")");
			blockLayer.setCell(x, y, null);
		} else {
			final Cell cell = new Cell();
			cell.setTile(blocks.get(block));
			blockLayer.setCell(x, y, cell);
		}
	}

	@Override
	public int getHeight() {
		return blockLayer.getHeight();
	}

	@Override
	public int getWidth() {
		return blockLayer.getWidth();
	}
	
	/**
	 * Inserts block into layer.
	 * @param block the block to insert
	 */
	public void insertBlock(Block block) {
			final int x = Math.round(block.getX());
			final int y = Math.round(block.getY());
			setBlock(x, y, block);
			System.out.println("Inserted " + block);
	}

	@Override
	public boolean hasBlock(int x, int y) {
		return blockLayer.getCell(x, y) != null 
				&& blockLayer.getCell(x, y).getTile() != null
				&& blockLayer.getCell(x, y).getTile().getTextureRegion() != null;
	}
	
	public Set<Block> getBlocks() {
		return blocks.keySet();
	}
	
	public BlockView getBlockView(Block block) {
		if (blocks.containsKey(block)) {
			return blocks.get(block);
		} else {
			return null;
		}
	}
	
	public Collection<BlockView> getBlockViews() {
		return blocks.values();
	}

	@Override
	public Set<Block> getActiveBlocks() {
		return activeBlocks;
	}

	@Override
	public void insertFinishedBlocks() {
		final Set<Block> doneBlocks = new HashSet<Block>();
		for (final Block block : activeBlocks) {
			if (block.getAnimationState().isDone()) {
				block.setAnimationState(AnimationState.NONE);
				insertBlock(block);
				doneBlocks.add(block);
			}
		}
		activeBlocks.removeAll(doneBlocks);
	}

	@Override
	public void addActiveBlock(Block block) {
		activeBlocks.add(block);
		System.out.println("Adding active "+block);
	}

	@Override
	public void updateActiveBlocks(float deltaTime) {
		for (final Block block : activeBlocks) {
			block.getAnimationState().updatePosition(deltaTime);
			System.out.println("Updating "+block);
			if(block.getAnimationState().isDone()) {
				System.out.println("Animation on "+block+" is done");
				block.moveToNextPosition();
			}
		}
	}
}