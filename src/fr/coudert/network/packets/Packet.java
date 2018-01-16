package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.utils.DataBuffer;

public abstract class Packet {

	public static final byte CONNECT = 0;
	public static final byte PING = 1;
	public static final byte SEED = 2;
	public static final byte DISCONNECT = 3;
	public static final byte NEW_PLAYER = 4;
	public static final byte UPDATE_POS = 5;
	public static final byte WEAPON_STATE = 6;
	public static final byte WEAPON_CHANGE = 7;
	public static final byte SHOOT = 8;
	public static final byte NEW_BULLET = 9;
	public static final byte HIT = 10;
	public static final byte MESSAGE = 11;

	protected DataBuffer data;

	public Packet(byte packetID) {
		data = new DataBuffer();
		data.put(packetID);
	}

	public abstract void read(DataBuffer buffer);
	public abstract void processS(InetAddress address, int port);
	public abstract void processC(InetAddress address);

	public DataBuffer getData() { return data; }

}