package fr.coudert.game.world.blocks;

import fr.coudert.game.models.Model;

public class ModelBlock extends Block {

	private Model model;

	public ModelBlock(Model model, boolean hitbox) {
		super(null, false, hitbox);
		this.model = model;
	}

	public Model getModel() { return model; }

}