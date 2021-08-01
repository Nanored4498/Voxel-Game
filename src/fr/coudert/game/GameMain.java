package fr.coudert.game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import fr.coudert.game.scenes.*;
import fr.coudert.network.Client;
import fr.coudert.rendering.*;
import fr.coudert.utils.Input;

public class GameMain {

	private static Scene scene;
	private static boolean antiAlias = true;
	private static String playerName;

	public static void update() {
		scene.update();
		Input.update();
	}

	public static void render() {
		glEnable(GL_MULTISAMPLE);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(Camera.fov, Camera.aspect, Camera.near, Camera.far);
		glMatrixMode(GL_MODELVIEW);
		scene.render();
		glDisable(GL_MULTISAMPLE);
	}

	public static void renderGUI() {
		Shader.unBind();
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluOrtho2D(0, Display.getWidth(), Display.getHeight(), 0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		scene.renderGUI();
	}

	public static void displayResized() {
		if(Display.wasResized()) {
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			Camera.aspect = (float)Display.getWidth() / (float)Display.getHeight();
			scene.displayResized();
		}
	}

	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(800, 450));
			Display.setTitle("Voxel");
			Display.setResizable(true);
			if(antiAlias)
				Display.create(new PixelFormat(2, 2, 0, 2));
			else 
				Display.create();
			Camera.aspect = (float)Display.getWidth()/(float)Display.getHeight();
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_CULL_FACE);
			glCullFace(GL_FRONT);
			glEnable(GL_TEXTURE_2D);
			glEnable(GL_ALPHA_TEST);
			glAlphaFunc(GL_GREATER, 0);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		scene = new Menu();
		SkyBox.updateVBO();
		final long dt = (long) 1e9 / 60;
		int frames = 0;
		long nextUpdate = System.nanoTime() + dt;
		long nextPing = System.nanoTime() + (long) 1e9;
		while(!Display.isCloseRequested()) {
			if(System.nanoTime() >= nextUpdate) {
				update();
				nextUpdate += dt;
				displayResized();
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				render();
				renderGUI();
				Display.update();
				frames ++;
			}
			if(System.nanoTime() >= nextPing) {
				if(Game.instance != null)
					Game.instance.updatePing(frames);
				frames = 0;
				nextPing += 1e9;
			}
			try {
				Thread.sleep(Math.max(1, (long) ((Math.min(nextUpdate, nextPing) - System.nanoTime()) / 1e6)));
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		Client.stop(true);
		Display.destroy();
		System.exit(0);
	}

	public static Scene getScene() { return scene; }
	public static void setScene(Scene s) { scene = s; }
	public static String getPlayerName() { return playerName; }
	public static void setPlayerName(String name) { playerName = name; }

}