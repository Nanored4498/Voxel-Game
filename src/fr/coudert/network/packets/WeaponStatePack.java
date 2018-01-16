package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.player.PlayerNet;
import fr.coudert.game.scenes.Game;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class WeaponStatePack extends Packet {

	private int id;
	private byte wIndex, wState;

	public WeaponStatePack() {
		super(WEAPON_STATE);
	}

	public WeaponStatePack(int id, byte wIndex, byte wState) {
		super(WEAPON_STATE);
		data.put(id);
		data.put(wIndex);
		data.put(wState);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		wIndex = buffer.getByte();
		wState = buffer.getByte();
	}

	public void processS(InetAddress address, int port) {
		ServerMain.getClient(id).weaponState = wState;
		Server.sendToAny(new WeaponStatePack(id, wIndex, wState), id);
	}

	public void processC(InetAddress address) {
		((PlayerNet) Game.instance.getEntitiesManager().get(id)).setWeaponState(wIndex, wState);
	}
	
}