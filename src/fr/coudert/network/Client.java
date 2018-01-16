package fr.coudert.network;

import java.io.IOException;
import java.net.*;

import fr.coudert.game.entities.player.Player;
import fr.coudert.network.packets.*;
import fr.coudert.utils.DataBuffer;

public class Client implements Runnable {

	private static boolean running = false;
	private static int port;
	private static InetAddress address;
	private static DatagramSocket socket;
	private static int ping; 

	public static void connect(String address, int port) {
		try {
			Client.address = InetAddress.getByName(address);
			Client.port = port;
			Client.socket = new DatagramSocket();
			System.out.println("Connecting to " + address + ":" + port);
			ping = 0;
			new Thread(new Client(), "receive-thread").start();
		} catch(UnknownHostException | SocketException e) {
			e.printStackTrace();
			stop(false);
		}
	}
	
	public void run() {
		running = true;
		while(running) {
			try {
				byte[] data = new byte[2048];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				socket.receive(packet);
				parsePacket(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void parsePacket(DatagramPacket receive) {
		DataBuffer data = new DataBuffer(receive.getData());
		Packet packet = PacketManager.getPacket(data.getByte());
		packet.read(data);
		packet.processC(receive.getAddress());
	}
	
	public static void send(DataBuffer data) {
		new Thread("send-thread") {
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(data.getData(), data.getData().length, address, port);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void send(Packet packet) {
		send(packet.getData());
	}
	
	public static void stop(boolean sendDisconnection) {
		running = false;
		if(sendDisconnection)
			send(new DisconnectPack(Player.localPlayer.id));
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		socket.close();
		System.out.println("Network has terminated !");
	}

	public static void setPing(int ping) { Client.ping = ping; }
	public static int getPing() { return ping; }

}