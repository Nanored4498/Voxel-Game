package fr.coudert.game.world.blocks;

import fr.coudert.game.models.Model;
import fr.coudert.rendering.Color;

public abstract class Block {

	public static final Block GRASS = new GrassBlock();
	public static final Block WEED = new ModelBlock(new Model("weed.vox"), false);
	public static final Block OAK_WOOD = new WoodBlock(0);
	public static final Block OAK_LEAF = new LeafBlock(0.6f);
	public static final Block FIR_WOOD = new WoodBlock(0.1f);
	public static final Block FIR_LEAF = new LeafBlock(0.4f);

	public static final int FACE_VERTICES = 4;
	public static final int DATA_SIZE = 6 * FACE_VERTICES * (3 + 4);
	public static final float S = 1;

	protected Color color;
	protected boolean opaque, hitbox; 

	public Block(Color color, boolean opaque, boolean hitbox) {
		this.color = color;
		this.opaque = opaque;
		this.hitbox = hitbox;
	}

	public Block setColor(Color color) {
		this.color = color;
		return this;
	}

	public float[] blockDataFront(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x, y, z,				color.r * s0 * 0.9f, color.g * s0 * 0.9f, color.b * s0 * 0.9f, color.a,
			x + S, y, z,			color.r * s1 * 0.9f, color.g * s1 * 0.9f, color.b * s1 * 0.9f, color.a,
			x + S, y + S, z,		color.r * s2 * 0.9f, color.g * s2 * 0.9f, color.b * s2 * 0.9f, color.a,
			x, y + S, z,			color.r * s3 * 0.9f, color.g * s3 * 0.9f, color.b * s3 * 0.9f, color.a
		};
	}

	public float[] blockDataBack(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x, y, z + S,			color.r * s0 * 0.9f, color.g * s0 * 0.9f, color.b * s0 * 0.9f, color.a,
			x, y + S, z + S,		color.r * s1 * 0.9f, color.g * s1 * 0.9f, color.b * s1 * 0.9f, color.a,
			x + S, y + S, z + S,	color.r * s2 * 0.9f, color.g * s2 * 0.9f, color.b * s2 * 0.9f, color.a,
			x + S, y, z + S,		color.r * s3 * 0.9f, color.g * s3 * 0.9f, color.b * s3 * 0.9f, color.a
		};
	}

	public float[] blockDataLeft(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x, y, z,				color.r * s0 * 0.8f, color.g * s0 * 0.8f, color.b * s0 * 0.8f, color.a,
			x, y + S, z,			color.r * s1 * 0.8f, color.g * s1 * 0.8f, color.b * s1 * 0.8f, color.a,
			x, y + S, z + S,		color.r * s2 * 0.8f, color.g * s2 * 0.8f, color.b * s2 * 0.8f, color.a,
			x, y, z + S,			color.r * s3 * 0.8f, color.g * s3 * 0.8f, color.b * s3 * 0.8f, color.a
			
		};
	}

	public float[] blockDataRight(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x + S, y, z,			color.r * s0 * 0.8f, color.g * s0 * 0.8f, color.b * s0 * 0.8f, color.a,
			x + S, y, z + S,		color.r * s1 * 0.8f, color.g * s1 * 0.8f, color.b * s1 * 0.8f, color.a,
			x + S, y + S, z + S,	color.r * s2 * 0.8f, color.g * s2 * 0.8f, color.b * s2 * 0.8f, color.a,
			x + S, y + S, z,		color.r * s3 * 0.8f, color.g * s3 * 0.8f, color.b * s3 * 0.8f, color.a
		};
	}

	public float[] blockDataDown(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x, y, z,				color.r * s0 * 0.7f, color.g * s0 * 0.7f, color.b * s0 * 0.7f, color.a,
			x, y, z + S,			color.r * s1 * 0.7f, color.g * s1 * 0.7f, color.b * s1 * 0.7f, color.a,
			x + S, y, z + S,		color.r * s2 * 0.7f, color.g * s2 * 0.7f, color.b * s2 * 0.7f, color.a,
			x + S, y, z,			color.r * s3 * 0.7f, color.g * s3 * 0.7f, color.b * s3 * 0.7f, color.a
		};
	}

	public float[] blockDataUp(float x, float y, float z, float s0, float s1, float s2, float s3) {
		return new float[] {
			x, y + S, z,			color.r * s0, color.g * s0, color.b * s0, color.a,
			x + S, y + S, z,		color.r * s1, color.g * s1, color.b * s1, color.a,
			x + S, y + S, z + S,	color.r * s2, color.g * s2, color.b * s2, color.a,
			x, y + S, z + S,		color.r * s3, color.g * s3, color.b * s3, color.a
		};
	}

	public Color getColor() { return color; }
	public Model getModel() { return null; }
	public boolean isOpaque() { return opaque; }
	public boolean haveHitBox() { return hitbox; }

}