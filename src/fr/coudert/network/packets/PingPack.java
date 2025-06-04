package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.network.Client;
import fr.coudert.network.ClientData;
import fr.coudert.utils.DataBuffer;

public class PingPack extends Packet {

	private int id;
	private int ping;

	public PingPack() {
		super(PING);
	}

	public PingPack(int id, int ping) {
		super(PING);
		data.put(id);
		data.put(ping);
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
		ping = buffer.getInt();
	}

	public void processS(InetAddress address, int port) {
		long time = System.currentTimeMillis();
		ClientData clientData = ServerMain.getClient(id);
		if(clientData == null)
			return;
		clientData.pingSent = 0;
		clientData.ping = (int) (time - clientData.pingTime);
	}

	public void processC(InetAddress address) {
		Client.send(new PingPack(id, 0));
		Client.setPing(ping);
	}

}
