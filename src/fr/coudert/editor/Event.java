package fr.coudert.editor;

import java.util.ArrayList;

import fr.coudert.game.world.blocks.Block;

public class Event {

	private Block[][][] blocks;
	private ArrayList<Block> addedBlocks;

	public Event(Block[][][] blocks,  ArrayList<Block> addedBlocks) {
		this.blocks = new Block[Editor.SIZE][Editor.SIZE][Editor.SIZE];
		for(int x = 0; x < Editor.SIZE; x++)
			for(int y = 0; y < Editor.SIZE; y++)
				for(int z = 0; z < Editor.SIZE; z++)
					this.blocks[x][y][z] = blocks[x][y][z];
		this.addedBlocks = new ArrayList<Block>();
		this.addedBlocks.addAll(addedBlocks);
	}

	public Block[][][] getBlocks() { return blocks; }
	public ArrayList<Block> getAddedBlocks() { return addedBlocks; }

}