package fr.coudert.network.packets;

import java.net.InetAddress;

import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.player.Player;
import fr.coudert.network.Server;
import fr.coudert.utils.DataBuffer;

public class MessagePack extends Packet {

	String name, text;

	public MessagePack() {
		super(MESSAGE);
	}

	public MessagePack(String name, String text) {
		super(MESSAGE);
		data.put(name);
		data.put(text);
		data.flip();
	}

	public void read(DataBuffer buffer) {
		name = buffer.getString();
		text = buffer.getString();
	}

	public void processS(InetAddress address, int port) {
		ServerMain.msg(name, text);
		Server.sendToAll(new MessagePack(name, text));
	}

	public void processC(InetAddress address) {
		Player.localPlayer.msg(name, text);
	}

}