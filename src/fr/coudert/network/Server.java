package fr.coudert.network;

import java.io.IOException;
import java.net.*;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

import fr.coudert.game.ServerMain;
import fr.coudert.network.commands.Command;
import fr.coudert.network.packets.*;
import fr.coudert.utils.DataBuffer;

public class Server {

	private static int port;
	private static DatagramSocket socket;
	private static Scanner scanner;

	public static void launch(int port) {
		Server.port = port;
		Server.scanner = new Scanner(System.in);
		ServerMain.print("Requesting connection on port " + port + "...");
		try {
			Server.socket = new DatagramSocket(port);
			ServerMain.print("Server successfully started on port " + port + " !");
			ServerMain.print("Type \"help\" to list every command.");
			ping();
			receive();
		} catch(SocketException e) {
			ServerMain.print("Server already listening on port: " + port);
			ServerMain.print("Server Failed to connect !");
			ServerMain.print("Terminating...");
		}
	}

	private static void ping() {
		new Thread("ping-thread") {
			public void run() {
				long before = System.currentTimeMillis();
				while(true) {
					if(System.currentTimeMillis() - before > 1000) {
						try {
							for(int key : ServerMain.getKeys()) {
								ClientData client = ServerMain.getClient(key);
								client.timeOuts ++;
								if(client.timeOuts > 5) {
									ServerMain.removeClient(client.id);
									sendToAll(new DisconnectPack(client.id));
									ServerMain.print(client.name + " was not answering, he has been disconnected");
									continue;
								}
								send(new PingPack(client.id, client.ping), client.address, client.port);
								client.pingTime = System.currentTimeMillis();
							}
							before += 1000;
						} catch(ConcurrentModificationException e) {}
					}
				}
			}
		}.start();
	}

	private static void receive() {
		new Thread("receive-thread") {
			public void run() {
				boolean rec = true;
				while(rec) {
					try {
						byte[] data = new byte[2048];
						DatagramPacket receive = new DatagramPacket(data, data.length);
						socket.receive(receive);
						parsePacket(receive);
					} catch (IOException e) {
						e.printStackTrace();
						if(socket.isClosed())
							rec = false;
					}
				}
			}
		}.start();
	}

	private static void parsePacket(DatagramPacket receive) {
		DataBuffer data = new DataBuffer(receive.getData());
		Packet packet = PacketManager.getPacket(data.getByte());
		packet.read(data);
		packet.processS(receive.getAddress(), receive.getPort());
	}

	public static void send(DataBuffer data, InetAddress address, int port) {
		new Thread("send-thread") {
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(data.getData(), data.getLength(), address, port);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void send(Packet packet, InetAddress address, int port) {
		send(packet.getData(), address, port);
	}

	public static void sendToAll(DataBuffer data) {
		for(int key : ServerMain.getKeys()) {
			ClientData client = ServerMain.getClient(key);
			send(data, client.address, client.port);
		}
	}

	public static void sendToAll(Packet packet) {
		sendToAll(packet.getData());
	}

	public static void sendToAny(DataBuffer data, int... ignoreID) {
		for(int key : ServerMain.getKeys()) {
			ClientData client = ServerMain.getClient(key);
			boolean ignored = false;
			for(int i : ignoreID)
				if(client.id == i) {
					ignored = true;
					break;
				}
			if(ignored)
				continue;
			send(data, client.address, client.port);
		}
	}

	public static void sendToAny(Packet packet, int... ignoreID) {
		sendToAny(packet.getData(), ignoreID);
	}

	public static void stop() {
		socket.close();
		ServerMain.print("Server stopped !");
	}

}