package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.network.ClientData;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class ConnectPack extends Packet {

	String name;

	public ConnectPack() {
		super(CONNECT);
	}

	public ConnectPack(String name) {
		super(CONNECT);
		data.put(name);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		name = buffer.getString();
	}

	public void processS(InetAddress address, int port) {
		ClientData clientData = ServerMain.getClientByName(name);
		if(clientData == null) {
			int id = ServerMain.addClient(name, address, port);
			Server.send(new SeedPack(ServerMain.getSeed(), ServerMain.getSpawnPos(), id), address, port);
			try {
				Thread.sleep(20);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			for(int key : ServerMain.getKeys())
				Server.send(new NewPlayerPack(ServerMain.getClient(key)), address, port);
		} else {
			Server.sendToAll(new NewPlayerPack(clientData));
			ServerMain.setInGame(clientData);
			ServerMain.msg(name, "is now connected");
		}
	}

	public void processC(InetAddress address) {}

}