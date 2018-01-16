package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.entities.player.PlayerNet;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.network.ClientData;
import fr.coudert.utils.DataBuffer;

public class NewPlayerPack extends Packet {

	private int id;
	private Vec3 pos;
	private Vec2 rot;
	private byte wIndex, wState;

	public NewPlayerPack() {
		super(NEW_PLAYER);
	}

	public NewPlayerPack(int id, Vec3 pos, Vec2 rot, byte wIndex, byte wState) {
		super(NEW_PLAYER);
		data.put(id);
		data.put(pos);
		data.put(rot);
		data.put(wIndex);
		data.put(wState);
		data.flip();
	}

	public NewPlayerPack(ClientData clientData) {
		this(clientData.id, clientData.pos, clientData.rot, clientData.weaponIndex, clientData.weaponState);
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		pos = buffer.getVec3();
		rot = buffer.getVec2();
		wIndex = buffer.getByte();
		wState = buffer.getByte();
	}

	public void processS(InetAddress address, int port) {}

	public void processC(InetAddress address) {
		Game.instance.getEntitiesManager().add(new PlayerNet(id, pos, rot, wIndex, wState));
	}

}
