package fr.coudert.game.scenes;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.game.GameMain;
import fr.coudert.game.entities.EntitiesManager;
import fr.coudert.game.entities.player.Player;
import fr.coudert.game.entities.player.PlayerNet;
import fr.coudert.game.world.World;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Client;
import fr.coudert.network.packets.ConnectPack;
import fr.coudert.rendering.FrustumCulling;
import fr.coudert.rendering.Shader;
import fr.coudert.rendering.guis.*;
import fr.coudert.utils.Input;

public class Game extends Scene {

	public static boolean debug;
	public static Game instance;

	private World world;
	private Player player;
	private EntitiesManager entitiesManager;
	private boolean pause;
	private GameMenu menu;
	private GuiText pingGui, ipsGui;
	private Vec3 spawnPos;

	public Game(Vec3 spawnPos, int localPlayerID) {
		instance = this;
		this.spawnPos = spawnPos.copy();
		player = new Player(localPlayerID, spawnPos);
		Player.localPlayer = player;
		entitiesManager = new EntitiesManager();
		entitiesManager.add(player);
		pause = false;
	}

	public void init(World world) {
		this.world = world;
		menu = new GameMenu(this);
		player.init();
		PlayerNet.initVBO();
		Client.send(new ConnectPack(GameMain.getPlayerName()));
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pingGui = (GuiText) new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 13), true), "", 10, 10).anchor(GuiComponent.TR);
		ipsGui = (GuiText) new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 13), true), "", 10, 25).anchor(GuiComponent.TR);
	}

	public void update() {
		if(Input.getKeyDown(Keyboard.KEY_ESCAPE) && Mouse.isGrabbed()) {
			pause = true;
			Mouse.setGrabbed(false);
		}
		if(Mouse.isGrabbed()) {
			if(Input.getKeyDown(Keyboard.KEY_F3))
				debug = ! debug;
			entitiesManager.update();
			world.update();
			FrustumCulling.update(this);
		} else {
			if(pause)
				menu.update();
			else if((Input.getMouseDown(0) && !Player.localPlayer.isMouseInChat()) || Input.getKeyDown(Keyboard.KEY_ESCAPE)) {
					Mouse.setGrabbed(true);
					Player.localPlayer.stopFocus();
			}
			Player.localPlayer.updateChat();
		}
	}

	public void render() {
		glRotatef(player.rot.x, 1, 0, 0);
		glRotatef(player.rot.y, 0, 1, 0);
		glTranslatef(-player.getEyesPos().x, -player.getEyesPos().y, -player.getEyesPos().z);
		Shader.SKYBOX.bind();
		world.getSkyBox().render(player.pos);
		Shader.unBind();
		entitiesManager.render();
		world.render();
		glLoadIdentity();
		player.renderFirst();
	}

	public void renderGUI() {
		if(pause)
			menu.renderGUI();
		else {
			glColor3f(0.2f, 0.2f, 0.2f);
			glRectf(Display.getWidth() / 2 - 2, Display.getHeight() / 2 - 2, Display.getWidth() / 2 + 2, Display.getHeight() / 2 + 2);
		}
		pingGui.renderGUI();
		ipsGui.renderGUI();
		player.renderGUI();
	}

	public void displayResized() {
		menu.displayResized();
		pingGui.anchorUpdate();
		ipsGui.anchorUpdate();
		player.displayResized();
	}

	public void updatePing(int ips) {
		if(pingGui == null)
			return;
		pingGui.setText(Client.getPing() + " ms");
		ipsGui.setText(ips + " ips");
	}

	public Player getPlayer() { return player; }
	public World getWorld() { return world; }
	public void setPause(boolean pause) { this.pause = pause; }
	public EntitiesManager getEntitiesManager() { return entitiesManager; }
	public Vec3 getSpawnPos() { return spawnPos; }

}