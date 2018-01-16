package fr.coudert.editor;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.Camera;
import fr.coudert.utils.Input;

public class EditorMain {

	public static Vec3 pos;
	public static Vec2 rot;
	public static Vec3 dir;

	public Editor editor;
	private float speed = 0.07f;

	public EditorMain() {
		editor = new Editor();
		pos = new Vec3(32, 5, 32);
		rot = new Vec2();
		dir = new Vec3();
	}

	public void update() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Mouse.isGrabbed())
			Mouse.setGrabbed(false);
		if(Input.getMouseDown(0) && !Mouse.isGrabbed() && Mouse.getX() > 200) {
			Mouse.setGrabbed(true);
			Input.update();
		}
		if(Mouse.isGrabbed()) {
			int dx = Mouse.getDX(), dy = Mouse.getDY();
			if(dx != 0 || dy != 0) {
				rot.x -= dy * 0.5f;
				rot.y += dx * 0.5f;
				float cosP = (float) Math.cos(Math.toRadians(-rot.x));
				dir = new Vec3((float) Math.cos(Math.toRadians(rot.y - 90)) * cosP, (float) Math.sin(Math.toRadians(-rot.x)), (float) Math.sin(Math.toRadians(rot.y - 90)) * cosP).normalized();
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_Z) && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				pos.z -= Math.cos(Math.toRadians(rot.y)) * speed;
				pos.x += Math.sin(Math.toRadians(rot.y)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
				pos.z += Math.cos(Math.toRadians(rot.y)) * speed;
				pos.x -= Math.sin(Math.toRadians(rot.y)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				pos.z += Math.cos(Math.toRadians(rot.y + 90)) * speed;
				pos.x -= Math.sin(Math.toRadians(rot.y + 90)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
				pos.z += Math.cos(Math.toRadians(rot.y - 90)) * speed;
				pos.x -= Math.sin(Math.toRadians(rot.y - 90)) * speed;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				pos.y += speed;
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
				pos.y -= speed;
		}
		editor.update();
		Input.update();
//		System.out.println(pos.x + " " + pos.y + " " + pos.z);
	}

	public void render() {
		glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(Camera.fov, Camera.aspect, Camera.near, Camera.far);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glRotatef(rot.x, 1, 0, 0);
		glRotatef(rot.y, 0, 1, 0);
		glTranslatef(-pos.x, -pos.y, -pos.z);
		editor.render();
	}

	public void renderGUI() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluOrtho2D(0, Display.getWidth(), Display.getHeight(), 0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		editor.renderGUI();
	}

	public static void main(String[] args) {
		try {
			Display.setDisplayMode(new DisplayMode(1280, 720));
			Display.setTitle("Voxel Editor");
			Display.setResizable(true);
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
		EditorMain main = new EditorMain();
		long before = System.nanoTime();
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(!Display.isCloseRequested()) {
			if(System.nanoTime() - before > ns) {
				main.update();
				before += ns;
			} else {
				if(Display.wasResized()) {
					glViewport(0, 0, Display.getWidth(), Display.getHeight());
					Camera.aspect = (float)Display.getWidth() / (float)Display.getHeight();
					main.editor.displayResized();
				}
				main.render();
				main.renderGUI();
				Display.update();
				frames ++;
			}
			if(System.currentTimeMillis() - timer > 1000) {
				System.out.println(frames + " fps");
				frames = 0;
				timer += 1000;
			}
		}
		Display.destroy();
		System.exit(0);
	}

}