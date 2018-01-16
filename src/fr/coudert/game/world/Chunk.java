package fr.coudert.game.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import fr.coudert.game.models.Model;
import fr.coudert.game.world.blocks.Block;
import fr.coudert.game.world.blocks.GrassBlock;
import fr.coudert.game.world.trees.Tree;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.Color;
import fr.coudert.rendering.FrustumCulling;

public class Chunk {

	public static final int SIZE = 16;
	public static final int HEIGHT = 32;
	public static final float RADIUS = SIZE * (float) Math.sqrt(3) / 2;
	public static final int MAX_UPDATES = 5;

	private static FloatBuffer buffer;
	private static final float ao = 0.7f;
	private static ArrayList<Block> grassblocks = new ArrayList<Block>();
	private static int updates = 0;

	private int vbo;
	private int x, z, height;
	private Vec3 centerPos;
	private float radius;
	private Block[][][] blocks;
	private ArrayList<Vec3> modelsPos;
	private int bufferSize;
	private World world;
	private boolean isInViewFrustum;
	private boolean needVBO, needUpdate;

	public Chunk(int xChunk, int zChunk, World world) {
		needVBO = true;
		this.x = xChunk;
		this.z = zChunk;
		this.world = world;
		blocks = new Block[SIZE][HEIGHT][SIZE];
		modelsPos = new ArrayList<Vec3>();
		height = 0;
		for(byte x = 0; x < SIZE; x++) {
			for(byte z = 0; z < SIZE; z++) {
				int xx = this.x * SIZE + x, zz = this.z * SIZE + z;
				int h = (int) world.getHeightNoise().getNoise(xx, zz);
				Block bl = grassblocks.get((int)(world.getColorNoise().getNoise(xx, zz)*255));
				for(byte y = 0; y <= h; y++)
					blocks[x][y][z] = bl;
				height = Math.max(height, h);
			}
		}
		centerPos = new Vec3(x * SIZE + SIZE / 2, height / 2, z * SIZE + SIZE / 2);
		radius = Math.max(RADIUS, (height - 1) / 2 * (float)Math.sqrt(2));
		isInViewFrustum = false;
	}

	public static void createArrayGrass() {
		Color yellow = new Color(0.4f, 0.8f, 0.2f);
		for(int i = 0; i < 256; i++) {
			grassblocks.add(new GrassBlock().setColor(Block.GRASS.getColor().interpolate(yellow, ((float)i)/255f)));
		}
	}

	public static void createBuffer() {
		buffer = BufferUtils.createFloatBuffer(SIZE * SIZE * HEIGHT * Block.DATA_SIZE / 4);
	}

	public void addVegetation() {
		int max = SIZE + 4;
		for(byte x = -4; x < max; x++) {
			for(byte z = -4; z < max; z++) {
				int xx = this.x * SIZE + x, zz = this.z * SIZE + z;
				if(world.getTreeNoise().getNoise(xx, zz) < 1)
					Tree.addTree(this, xx, (int) world.getHeightNoise().getNoise(xx, zz) + 1, zz, (int) (world.getColorNoise().getNoise(xx, zz) * 2));
			}
		}
		needUpdate = true;
	}

	private void updateVBO() {
		if(needVBO) {
			vbo = glGenBuffers();
			needVBO = false;
		}
		bufferSize = 0;
		addFacesToBuffer();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	private void addFacesToBuffer() {
		buffer.clear();
		modelsPos.clear();
		int a = this.x * SIZE, b = this.z * SIZE;
		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < SIZE; z++) {
					Block block = blocks[x][y][z];
					if(block == null)
						continue;
					int xx = a + x, zz = b + z;
					boolean up = !world.isOpaque(xx, y + 1, zz);
					boolean down = !world.isOpaque(xx, y - 1, zz);
					boolean left = !world.isOpaque(xx - 1, y, zz);
					boolean right = !world.isOpaque(xx + 1, y, zz);
					boolean front = !world.isOpaque(xx, y, zz - 1);
					boolean back = !world.isOpaque(xx, y, zz + 1);
					Model model = block.getModel();
					if(model != null) {
						if(up || down || left || right || front || back)
							modelsPos.add(new Vec3(xx, y, zz));
						continue;
					}
					if(up) {
						boolean bX = world.isOpaque(xx + 1, y + 1, zz);
						boolean bx = world.isOpaque(xx - 1, y + 1, zz);
						boolean bZ = world.isOpaque(xx, y + 1, zz + 1);
						boolean bz = world.isOpaque(xx, y + 1, zz - 1);
						boolean bXZ = world.isOpaque(xx + 1, y + 1, zz + 1);
						boolean bXz = world.isOpaque(xx + 1, y + 1, zz - 1);
						boolean bxZ = world.isOpaque(xx - 1, y + 1, zz + 1);
						boolean bxz = world.isOpaque(xx - 1, y + 1, zz - 1);
						buffer.put(block.blockDataUp(xx, y, zz, (bx || bz || bxz)? ao : 1, (bX || bz || bXz)? ao : 1, (bX || bZ || bXZ)? ao : 1, (bx || bZ || bxZ)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
					if(down) {
						boolean bX = world.isOpaque(xx + 1, y - 1, zz);
						boolean bx = world.isOpaque(xx - 1, y - 1, zz);
						boolean bZ = world.isOpaque(xx, y - 1, zz + 1);
						boolean bz = world.isOpaque(xx, y - 1, zz - 1);
						boolean bXZ = world.isOpaque(xx + 1, y - 1, zz + 1);
						boolean bXz = world.isOpaque(xx + 1, y - 1, zz - 1);
						boolean bxZ = world.isOpaque(xx - 1, y - 1, zz + 1);
						boolean bxz = world.isOpaque(xx - 1, y - 1, zz - 1);
						buffer.put(block.blockDataDown(xx, y, zz, (bx || bz || bxz)? ao : 1, (bx || bZ || bxZ)? ao : 1, (bX || bZ || bXZ)? ao : 1, (bX || bz || bXz)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
					if(left) {
						boolean bY = world.isOpaque(xx - 1, y + 1, zz);
						boolean by = world.isOpaque(xx - 1, y - 1, zz);
						boolean bZ = world.isOpaque(xx - 1, y, zz + 1);
						boolean bz = world.isOpaque(xx - 1, y, zz - 1);
						boolean bYZ = world.isOpaque(xx - 1, y + 1, zz + 1);
						boolean bYz = world.isOpaque(xx - 1, y + 1, zz - 1);
						boolean byZ = world.isOpaque(xx - 1, y - 1, zz + 1);
						boolean byz = world.isOpaque(xx - 1, y - 1, zz - 1);
						buffer.put(block.blockDataLeft(xx, y, zz, (by || bz || byz)? ao : 1, (bY || bz || bYz)? ao : 1, (bY || bZ || bYZ)? ao : 1, (by || bZ || byZ)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
					if(right) {
						boolean bY = world.isOpaque(xx + 1, y + 1, zz);
						boolean by = world.isOpaque(xx + 1, y - 1, zz);
						boolean bZ = world.isOpaque(xx + 1, y, zz + 1);
						boolean bz = world.isOpaque(xx + 1, y, zz - 1);
						boolean bYZ = world.isOpaque(xx + 1, y + 1, zz + 1);
						boolean bYz = world.isOpaque(xx + 1, y + 1, zz - 1);
						boolean byZ = world.isOpaque(xx + 1, y - 1, zz + 1);
						boolean byz = world.isOpaque(xx + 1, y - 1, zz - 1);
						buffer.put(block.blockDataRight(xx, y, zz, (by || bz || byz)? ao : 1, (by || bZ || byZ)? ao : 1, (bY || bZ || bYZ)? ao : 1, (bY || bz || bYz)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
					if(front) {
						boolean bX = world.isOpaque(xx + 1, y, zz - 1);
						boolean bx = world.isOpaque(xx - 1, y, zz - 1);
						boolean bY = world.isOpaque(xx, y + 1, zz - 1);
						boolean by = world.isOpaque(xx, y - 1, zz - 1);
						boolean bXY = world.isOpaque(xx + 1, y + 1, zz - 1);
						boolean bXy = world.isOpaque(xx + 1, y - 1, zz - 1);
						boolean bxY = world.isOpaque(xx - 1, y + 1, zz - 1);
						boolean bxy = world.isOpaque(xx - 1, y - 1, zz - 1);
						buffer.put(block.blockDataFront(xx, y, zz, (bx || by || bxy)? ao : 1, (bX || by || bXy)? ao : 1, (bX || bY || bXY)? ao : 1, (bx || bY || bxY)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
					if(back) {
						boolean bX = world.isOpaque(xx + 1, y, zz + 1);
						boolean bx = world.isOpaque(xx - 1, y, zz + 1);
						boolean bY = world.isOpaque(xx, y + 1, zz + 1);
						boolean by = world.isOpaque(xx, y - 1, zz + 1);
						boolean bXY = world.isOpaque(xx + 1, y + 1, zz + 1);
						boolean bXy = world.isOpaque(xx + 1, y - 1, zz + 1);
						boolean bxY = world.isOpaque(xx - 1, y + 1, zz + 1);
						boolean bxy = world.isOpaque(xx - 1, y - 1, zz + 1);
						buffer.put(block.blockDataBack(xx, y, zz, (bx || by || bxy)? ao : 1, (bx || bY || bxY)? ao : 1, (bX || bY || bXY)? ao : 1, (bX || by || bXy)? ao : 1));
						bufferSize += Block.FACE_VERTICES;
					}
				}
			}
		}
		buffer.flip();
	}

	public void update() {
		if(!needVBO) {
			isInViewFrustum = FrustumCulling.isInViewFrustum(centerPos, radius);
		}
		if(needUpdate && updates < MAX_UPDATES) {
			updateVBO();
			needUpdate = false;
			updates ++;
		}
	}

	public void render() {
		if(!isInViewFrustum)
			return;
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * 4, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * 4, 12);
		glDrawArrays(GL_QUADS, 0, bufferSize);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		for(Vec3 pos : modelsPos)
			world.getBlock((int) pos.x, (int) pos.y, (int) pos.z).getModel().render(pos.x, pos.y, pos.z);
	}

	public void renderGUI() {
		
	}

	public Block getBlock(int x, int y, int z) {
		if(x < 0 || y < 0 || z < 0 || x >= SIZE || y >= height || z >= SIZE)
			return null;
		return blocks[x][y][z];
	}

	public void setBlock(int x, int y, int z, Block block) {
		if(x < 0 || y < 0 || z < 0 || x >= SIZE || y >= HEIGHT || z >= SIZE)
			return;
		blocks[x][y][z] = block;
		if(block == null) {
			if(y == height - 1)
				if(!cheksBlocks(y))
					for(int i = y - 1; i > 0; i--)
						if(cheksBlocks(i)) {
							height = i + 1;
							updateRadius();
							i = 0;
						}
		} else {
			if(height <= y) {
				height = y + 1;
				updateRadius();
			}
		}
		if(x == 0) {
			Chunk c = world.getChunk(this.x - 1, this.z);
			if(c != null)
				c.needUpdate = true;
		}
		if(x == SIZE - 1) {
			Chunk c = world.getChunk(this.x + 1, this.z);
			if(c != null)
				c.needUpdate = true;
		}
		if(z == 0) {
			Chunk c = world.getChunk(this.x, this.z - 1);
			if(c != null)
				c.needUpdate = true;
		}
		if(z == SIZE - 1) {
			Chunk c = world.getChunk(this.x, this.z + 1);
			if(c != null)
				c.needUpdate = true;
		}
	}

	private boolean cheksBlocks(int y) {
		for(int x = 0; x < SIZE; x++)
			for(int z = 0; z < SIZE; z++)
				if(blocks[x][y][z] != null)
					return true;
		return false;
	}

	private void updateRadius() {
		centerPos.y = (height - 1) / 2;
		radius = Math.max(RADIUS, height / 2 * (float)Math.sqrt(3));
	}

	public void delete() {
		glDeleteBuffers(vbo);
		blocks = null;
	}

	public void forceUpdate() {
		needUpdate = true;
	}

	public static void resetUpdates() {
		updates = 0;
	}

	public int getX() { return x; }
	public int getZ() { return z; }

}