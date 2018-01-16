package fr.coudert.network.packets;

import java.util.HashMap;
import java.util.Map;

public class PacketManager {

private static Map<Byte, Class<? extends Packet>> packets;
	
	static {
		packets = new HashMap<Byte, Class<? extends Packet>>();
		packets.put(Packet.CONNECT, ConnectPack.class);
		packets.put(Packet.PING, PingPack.class);
		packets.put(Packet.SEED, SeedPack.class);
		packets.put(Packet.DISCONNECT, DisconnectPack.class);
		packets.put(Packet.NEW_PLAYER, NewPlayerPack.class);
		packets.put(Packet.UPDATE_POS, UpdatePosPack.class);
		packets.put(Packet.WEAPON_STATE, WeaponStatePack.class);
		packets.put(Packet.WEAPON_CHANGE, WeaponChangePack.class);
		packets.put(Packet.SHOOT, ShootPack.class);
		packets.put(Packet.NEW_BULLET, NewBulletPack.class);
		packets.put(Packet.HIT, HitPack.class);
		packets.put(Packet.MESSAGE, MessagePack.class);
	}
	
	public static Packet getPacket(byte packet) {
		try {
			return (Packet) packets.get(packet).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}