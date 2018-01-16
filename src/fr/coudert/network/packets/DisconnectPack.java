package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.player.Player;
import fr.coudert.game.scenes.Game;
import fr.coudert.network.Client;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class DisconnectPack extends Packet {

	private int id;

	public DisconnectPack() {
		super(DISCONNECT);
	}

	public DisconnectPack(int id) {
		super(DISCONNECT);
		data.put(id);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		id = buffer.getInt();
	}

	public void processS(InetAddress address, int port) {
		ServerMain.msg(ServerMain.getClient(id).name, "is now disconnected");
		ServerMain.removeClient(id);
		Server.sendToAll(new DisconnectPack(id));
	}

	public void processC(InetAddress address) {
		if(Player.localPlayer.id == id)
			Client.stop(false);
		Game.instance.getEntitiesManager().get(id).destroyed();
	}

}
