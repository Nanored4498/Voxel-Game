package fr.coudert.game.world.blocks;

import fr.coudert.rendering.Color;

public class WoodBlock extends Block {

	public WoodBlock(float f) {
		super(new Color(0.42f - f, 0.32f - f, 0.14f - f), true, true);
	}

}