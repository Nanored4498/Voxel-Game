package fr.coudert.game;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Font;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import fr.coudert.maths.Vec3;
import fr.coudert.network.ClientData;
import fr.coudert.network.Server;
import fr.coudert.network.commands.Command;
import fr.coudert.network.packets.MessagePack;
import fr.coudert.rendering.Camera;
import fr.coudert.rendering.Color;
import fr.coudert.rendering.guis.Gui;
import fr.coudert.rendering.guis.GuiArea;
import fr.coudert.rendering.guis.GuiInputField;
import fr.coudert.rendering.guis.TrueTypeFont;
import fr.coudert.utils.Input;

public class ServerMain {

	private static long seed;
	private static Vec3 spawnPos;
	private static int id;
	private static ArrayList<Integer> keys;
	private static Map<Integer, ClientData> clients;
	private static ArrayList<ClientData> newClients;
	private static GuiArea chat;

	//TODO: Régler BUG du depth de l'affichage
	public static void main(String[] args) {
		//Initialisation interface
		try {
			Display.setDisplayMode(new DisplayMode(600, 500));
			Display.setTitle("Serveur");
			Display.setResizable(true);
			Display.create();
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_ALPHA_TEST);
			glAlphaFunc(GL_GREATER, 0);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		chat = new GuiArea(new TrueTypeFont(new Font("Arial", Font.PLAIN, 16), true), 0, 0, 300, 400);
		GuiInputField input = new GuiInputField(0, 405, 300, 28) {
			public void enter() {
				String text = getText();
				if(!Command.execute(text.split(" "))) {
					ServerMain.msg("[SERVER]", text);
					Server.sendToAll(new MessagePack("[SERVER]", text));
				}
			}
		};
		TrueTypeFont playerF = new TrueTypeFont(new Font("Arial", Font.PLAIN, 18), true);
		//Initialisation serveur
		seed = 0;
		spawnPos = new Vec3(5, 64, 5);
		id = 0;
		keys = new ArrayList<Integer>();
		clients = new HashMap<Integer, ClientData>();
		newClients = new ArrayList<ClientData>();
		Server.launch(4498);
		//boucle main
		long before = System.nanoTime();
		final double ns = 1e9 / 25.0;
		while(!Display.isCloseRequested()) {
			if(System.nanoTime() - before > ns) {
				final float w = Display.getWidth(), h = Display.getHeight();
				chat.update();
				input.update();
				Input.update();
				before += ns;
				if(Display.wasResized()) {
					glViewport(0, 0, (int)w, (int)h);
					Camera.aspect = w / h;
				}
				glClear(GL_COLOR_BUFFER_BIT);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				GLU.gluOrtho2D(0, w, h, 0);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				Gui.color(Color.CHAT);
				Gui.renderQuad(0, 0, w, h);
				chat.renderGUI();
				input.renderGUI();
				Gui.color(0.2f, 0.8f, 0.1f, 1);
				int i = 0;
				for(int key : keys) {
					ClientData client = clients.get(key);
					playerF.drawString(w-2-playerF.getWidth(client.name), 2+i*playerF.getHeight(), client.name);
					i++;
				}
				Display.update();
			} else {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Server.stop();
		Display.destroy();
		System.exit(0);
	}

	public static int addClient(String name, InetAddress address, int port) {
		newClients.add(new ClientData(id, name, address, port));
		return id++;
	}

	public static void setInGame(ClientData clientData) {
		clients.put(clientData.id, clientData);
		keys.add(clientData.id);
		newClients.remove(clientData);
	}

	public static ClientData getClientByName(String name) {
		for(ClientData clientData : newClients)
			if(clientData.name.equals(name))
				return clientData;
		return null;
	}

	public static void removeClient(int id) {
		keys.remove((Integer) id);
		clients.remove(id);
	}

	public static void print(String text) {
		chat.addLine(GuiArea.infoC, new String[] {text});
	}

	public static void msg(String name, String text) {
		chat.addLine(GuiArea.messC, new String[] {name + " : ", text});
	}

	public static long getSeed() { return seed; }
	public static Vec3 getSpawnPos() { return spawnPos; }
	public static ArrayList<Integer> getKeys() { return keys; }
	public static ClientData getClient(int i) { return clients.get(i); }
	public static int getNewID() { return id++; }

}