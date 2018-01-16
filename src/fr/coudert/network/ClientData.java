package fr.coudert.network;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.objects.Weapon;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;

public class ClientData {

	public int id;
	public String name;
	public InetAddress address;
	public int port;
	public int ping, timeOuts;
	public long pingTime;
	public Vec3 pos;
	public Vec2 rot;
	public byte weaponIndex, weaponState;

	public ClientData(int id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
		ping = timeOuts = 0;
		pingTime = 0;
		rot = new Vec2();
		pos = ServerMain.getSpawnPos();
		weaponIndex = 0;
		weaponState = Weapon.IDLE;
	}

}