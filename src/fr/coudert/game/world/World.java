package fr.coudert.game.world;

import java.util.*;

import org.lwjgl.input.Keyboard;

import fr.coudert.game.GameMain;
import fr.coudert.game.entities.player.Player;
import fr.coudert.game.scenes.LoadingWorld;
import fr.coudert.game.world.blocks.Block;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.*;
import fr.coudert.utils.Input;

public class World {

	public final float GRAVITY = 0.00073f;

	private long seed;
	private Map<Integer, Map<Integer, Chunk>> chunks;
	private Noise heightNoise, colorNoise, treeNoise;
	private SkyBox skyBox;
	private int xMin, xMax, zMin, zMax;
	private int tick = 0;

	public World(long seed, LoadingWorld loadScene) {
		this.seed = seed;
		skyBox = SkyBox.SKY_1;
		xMin = zMin = -8;
		xMax = zMax = 8;
		GameMain.setScene(loadScene);
		heightNoise = new Noise(seed, 40, 10);
		colorNoise = new Noise(seed, 40, 1);
		treeNoise = new Noise(seed, 1, 420);
		float add = 1 / ((float)(2*xMax+1));
		float pro = 0;
		chunks = new TreeMap<>();
		for(int x = xMin; x <= xMax; x++) {
			loadScene.setInfos("Creating chunks", pro);
			chunks.put(x, new TreeMap<>());
			for(int z = zMin; z <= zMax; z++)
				chunks.get(x).put(z, new Chunk(x, z, this));
			pro += add;
		}
		pro = 0;
		for(int x = xMin; x <= xMax; x++) {
			loadScene.setInfos("Adding vegetation", pro);
			for(int z = zMin; z <= zMax; z++)
				chunks.get(x).get(z).addVegetation();
			pro += add;
		}
		Chunk.createBuffer();
	}

	public float createVBOs(int x) {
		for(int z = zMin; z <= zMax; z++) {
			chunks.get(x).get(z).update();
			Chunk.resetUpdates();
		}
		return ((float) x + 8f) / 17f;
	}

	public void update() {
		tick ++;
		if(tick == 150) {
			tick = 0;
			int xMin2 = (int) ((Player.localPlayer.pos.x - Camera.far / 1.72f) / Chunk.SIZE),
					xMax2 = (int) ((Player.localPlayer.pos.x + Camera.far / 1.72f) / Chunk.SIZE),
					zMin2 = (int) ((Player.localPlayer.pos.z - Camera.far / 1.72f) / Chunk.SIZE),
					zMax2 = (int) ((Player.localPlayer.pos.z + Camera.far / 1.72f) / Chunk.SIZE);
			for(int x = xMin; x <= xMax; x++) {
				boolean b = x < xMin2 || x > xMax2;
				for(int z = zMin; z <= zMax; z++)
					if((b || z < zMin2 || z >zMax2) && chunks.get(x).get(z) != null) {
						chunks.get(x).get(z).delete();
						chunks.get(x).remove(z);
					}
				if(b)
					chunks.remove(x);
			}
			ArrayList<Vec2> poss = new ArrayList<Vec2>();
			for(int x = xMin2; x <= xMax2; x++) {
				boolean b = x < xMin || x > xMax;
				if(b)
					chunks.put(x, new TreeMap<>());
				for(int z = zMin2; z <= zMax2; z++)
					if(b || z < zMin || z >zMax)
						poss.add(new Vec2(x, z));
			}
			new Thread(new AddChunk(poss, this)).start();
			xMin = xMin2;
			xMax = xMax2;
			zMin = zMin2;
			zMax = zMax2;
		}
		if(Input.getKeyDown(Keyboard.KEY_F5))
			for(int x = xMin; x <= xMax; x++)
				for(int z = zMin; z <= zMax; z++)
					chunks.get(x).get(z).forceUpdate();
		Chunk.resetUpdates();
		for(int x = xMin; x <= xMax; x++)
			for(int z = zMin; z <= zMax; z++) {
				Chunk c = chunks.get(x).get(z);
				if(c != null)
					c.update();
			}
	}

	public void render() {
		Shader.MAIN.bind();
		Shader.MAIN.setUniform("fogColor", new Vec3(0.867f, 0.91f, 1.0f));
		Shader.MAIN.setUniform("camPos", new Vec3(Player.localPlayer.pos.x, Player.localPlayer.pos.y, Player.localPlayer.pos.z));
		Shader.MAIN.setUniform("viewDist", Camera.far);
		for(int x = xMin; x <= xMax; x++)
			for(int z = zMin; z <= zMax; z++) {
				Chunk c = chunks.get(x).get(z);
				if(c != null)
					c.render();
			}
	}

	public void renderGUI() {
		
	}

	public Block getBlock(int x, int y, int z) {
		int xChunk = x < 0 ? (x+1) / Chunk.SIZE - 1 : x / Chunk.SIZE, zChunk = z < 0 ? (z+1) / Chunk.SIZE - 1 : z / Chunk.SIZE;
		if(xChunk < xMin || xChunk > xMax)
			return null;
		Chunk c = chunks.get(xChunk).get(zChunk);
		if(c == null)
			return null;
		else
			return c.getBlock(x - xChunk*Chunk.SIZE, y, z - zChunk*Chunk.SIZE);
	}

	public boolean isOpaque(int x, int y, int z) {
		Block b = getBlock(x, y, z);
		if(b == null)
			return false;
		return b.isOpaque();
	}

	public boolean haveHitbox(int x, int y, int z) {
		Block b = getBlock(x, y, z);
		if(b == null)
			return false;
		return b.haveHitBox();
	}

	public void setBlock(int x, int y, int z, Block block) {
		int xChunk = x < 0 ? (x+1) / Chunk.SIZE - 1 : x / Chunk.SIZE, zChunk = z < 0 ? (z+1) / Chunk.SIZE - 1 : z / Chunk.SIZE;
		if(xChunk < xMin || xChunk > xMax)
			return;
		Chunk c = chunks.get(xChunk).get(zChunk);
		if(c == null)
			return;
		else
			c.setBlock(x - xChunk*Chunk.SIZE, y, z - zChunk*Chunk.SIZE, block);
	}

	public Chunk getChunk(int x, int z) {
		if(x < xMin || x > xMax)
			return null;
		else
			return chunks.get(x).get(z);
	}

	public long getSeed() { return seed; }
	public SkyBox getSkyBox() { return skyBox; }
	public Noise getColorNoise() { return colorNoise; }
	public Noise getHeightNoise() { return heightNoise; }
	public Noise getTreeNoise() { return treeNoise; }

	public class AddChunk implements Runnable {

		ArrayList<Vec2> poss;
		World world;
		
		public AddChunk(ArrayList<Vec2> poss, World world) {
			this.poss = poss;
			this.world = world;
		}
	
		public void run() {
			for(Vec2 pos : poss) {
				Chunk chunk = new Chunk((int) pos.x, (int) pos.y, world);
				world.chunks.get((int) pos.x).put((int) pos.y, chunk);
			}
			for(Vec2 pos : poss)
				world.chunks.get((int) pos.x).get((int) pos.y).addVegetation();
		}
		
	}

}