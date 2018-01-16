package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.entities.particles.ParticleSystem;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Server;
import fr.coudert.rendering.Color;
import fr.coudert.utils.DataBuffer;

public class HitPack extends Packet {

	private int bulletID, playerID;
	private Vec3 pos;

	public HitPack() {
		super(HIT);
	}

	public HitPack(int bulletId, int playerId, Vec3 pos) {
		super(HIT);
		data.put(bulletId);
		data.put(playerId);
		data.put(pos);
	}

	public void read(DataBuffer buffer) {
		bulletID = buffer.getInt();
		playerID = buffer.getInt();
		pos = buffer.getVec3();
	}

	public void processS(InetAddress address, int port) {
		Server.sendToAny(new HitPack(bulletID, playerID, pos));
	}

	public void processC(InetAddress address) {
		Game.instance.getEntitiesManager().add(
				new ParticleSystem(bulletID, pos, Game.instance.getEntitiesManager().get(bulletID).dir.normalized().mul(-0.09f), 30, 100, Color.BLOOD));
	}

}
