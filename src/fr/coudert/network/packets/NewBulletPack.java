package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.entities.particles.Bullet;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.utils.DataBuffer;

public class NewBulletPack extends Packet {

	private int id, playerID;
	private Vec3 pos;
	private Vec2 rot;
	private byte wIndex;

	public NewBulletPack() {
		super(NEW_BULLET);
	}

	public NewBulletPack(int id, int playerID, Vec3 pos, Vec2 rot, byte wIndex) {
		super(NEW_BULLET);
		data.put(id);
		data.put(playerID);
		data.put(pos);
		data.put(rot);
		data.put(wIndex);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		playerID = buffer.getInt();
		pos = buffer.getVec3();
		rot = buffer.getVec2();
		wIndex = buffer.getByte();
	}

	public void processS(InetAddress address, int port) { }

	public void processC(InetAddress address) {
		Game.instance.getEntitiesManager().add(new Bullet(id, pos, rot, 3.8f, 0.05f, playerID, wIndex));
	}

}
