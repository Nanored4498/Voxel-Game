package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.maths.Vec3;
import fr.coudert.network.ClientData;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class ShootPack extends Packet {

	private int id;

	public ShootPack() {
		super(SHOOT);
	}

	public ShootPack(int id) {
		super(SHOOT);
		data.put(id);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
	}

	public void processS(InetAddress address, int port) {
		ClientData clientData = ServerMain.getClient(id);
		Vec3 pos = clientData.pos.copy();
		pos.y += 1.13f;
		Server.sendToAll(new NewBulletPack(ServerMain.getNewID(), id, pos, clientData.rot, clientData.weaponIndex));
	}

	public void processC(InetAddress address) { }

}