package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.player.PlayerNet;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.network.ClientData;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class UpdatePosPack extends Packet {

	private int id;
	private Vec3 pos;
	private Vec2 rot;

	public UpdatePosPack() {
		super(UPDATE_POS);
	}

	public UpdatePosPack(int id, Vec3 pos, Vec2 rot) {
		super(UPDATE_POS);
		data.put(id);
		data.put(pos);
		data.put(rot);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		pos = buffer.getVec3();
		rot = buffer.getVec2();
	}

	public void processS(InetAddress address, int port) {
		ClientData clientData = ServerMain.getClient(id);
		clientData.pos = pos;
		clientData.rot = rot;
		Server.sendToAny(new UpdatePosPack(id, pos, rot), id);
	}

	public void processC(InetAddress address) {
		((PlayerNet) Game.instance.getEntitiesManager().get(id)).updatePos(pos, rot);
	}

}
