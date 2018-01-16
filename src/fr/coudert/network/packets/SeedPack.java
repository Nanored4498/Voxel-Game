package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.GameMain;
import fr.coudert.game.scenes.Game;
import fr.coudert.game.scenes.LoadingWorld;
import fr.coudert.maths.Vec3;
import fr.coudert.utils.DataBuffer;

public class SeedPack extends Packet {

	private long seed;
	private Vec3 spawnPos;
	private int playerID;

	public SeedPack() {
		super(SEED);
	}

	public SeedPack(long seed, Vec3 spawnPos, int playerID) {
		super(SEED);
		data.put(seed);
		data.put(spawnPos);
		data.put(playerID);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		seed = buffer.getLong();
		spawnPos = buffer.getVec3();
		playerID = buffer.getInt();
	}

	public void processS(InetAddress address, int port) {}

	public void processC(InetAddress address) {
		new Game(spawnPos, playerID);
		LoadingWorld loadingWorld = (LoadingWorld) GameMain.getScene();
		loadingWorld.createWorld(seed);
		loadingWorld.loadGame();
	}

}
