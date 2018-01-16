package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.player.PlayerNet;
import fr.coudert.game.scenes.Game;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class WeaponChangePack extends Packet {

	private int id;
	private byte wIndex;

	public WeaponChangePack() {
		super(WEAPON_CHANGE);
	}

	public WeaponChangePack(int id, byte wIndex) {
		super(WEAPON_CHANGE);
		data.put(id);
		data.put(wIndex);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		wIndex = buffer.getByte();
	}

	public void processS(InetAddress address, int port) {
		ServerMain.getClient(id).weaponIndex = wIndex;
		Server.sendToAny(new WeaponChangePack(id, wIndex), id);
	}

	public void processC(InetAddress address) {
		((PlayerNet) Game.instance.getEntitiesManager().get(id)).setWeaponIndex(wIndex);
	}

}