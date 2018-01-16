package fr.coudert.network.commands;

import fr.coudert.game.ServerMain;
import fr.coudert.network.*;
import fr.coudert.network.packets.DisconnectPack;

public class KickCmd extends Command {

	protected KickCmd() {
		super("kick", "kick the player out of this server");
	}

	protected void process(String... params) {
		for(int key : ServerMain.getKeys()) {
			ClientData clientData = ServerMain.getClient(key);
			if(clientData.name.equals(params[1])) {
				Server.sendToAll(new DisconnectPack(key));
				ServerMain.removeClient(key);
				System.out.println(clientData.name + " has been kicked");
				return;
			}
		}
	}

}