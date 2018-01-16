package fr.coudert.game.world.blocks;

import fr.coudert.rendering.Color;

public class LeafBlock extends Block {

	public LeafBlock(float g) {
		super(new Color(0, g, 0), true, true);
	}

}