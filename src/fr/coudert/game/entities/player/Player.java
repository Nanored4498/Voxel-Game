package fr.coudert.game.entities.player;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.util.ArrayList;

import javax.lang.model.element.NestingKind;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.game.GameMain;
import fr.coudert.game.ServerMain;
import fr.coudert.game.entities.Entity;
import fr.coudert.game.objects.*;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Client;
import fr.coudert.network.packets.*;
import fr.coudert.rendering.Camera;
import fr.coudert.rendering.Texture;
import fr.coudert.rendering.guis.*;
import fr.coudert.utils.Input;

public class Player extends Entity {

	public static Player localPlayer;

	private float xa, ya, za;
	private float xDir, zDir;
	private float speed;
	private float eyesHeight;
	private ArrayList<Weapon> weapons;
	private byte wIndex;
	private boolean wUp;
	private byte count;
	private float health;
	private GuiText healthText, posText;
	private GuiProgressBar healthBar;
	private GuiArea chat;
	private GuiInputField input;
	private boolean showDamageEffect;
	private float alpha, xAlpha;
	private Vec2 c1, c2, c3, c4;

	public Player(int id, Vec3 pos) {
		super(id, pos, 10.0f, 0.3f);
		height = 1.25f;
		eyesHeight = height - 0.12f;
		count = 0;
		health = 100;
		showDamageEffect = false;
		alpha = 0;
		c1 = new Vec2();
		c2 = new Vec2();
		c3 = new Vec2();
		c4 = new Vec2();
	}

	public void init() {
		weapons = new ArrayList<Weapon>();
		weapons.add(new Gun());
		weapons.add(new Ak47());
		weapons.add(new Sniper());
		wIndex = 0;
		weapons.get(wIndex).setState(Weapon.IDLE, false);
		healthText = (GuiText) new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 25), true), "100/100", 20, 15)
				.anchor(GuiComponent.BL);
		posText = new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, 13), true), "", 5, 5);
		healthBar = (GuiProgressBar) new GuiProgressBar(12, 12, 225, 35).anchor(GuiComponent.BL);
		healthBar.setProgression(1.0f);
		chat = (GuiArea) new GuiArea(new TrueTypeFont(new Font("Arial", Font.PLAIN, 12), true), 8, 71, 230, 130)
				.anchor(GuiComponent.BL);
		chat.setVisibility(0.2f);
		input = (GuiInputField) new GuiInputField(8, 50, 230, 20) {
			public void enter() {
				Client.send(new MessagePack(GameMain.getPlayerName(), getText()));
			}
		}.anchor(GuiComponent.BL);
	}

	public void update() {
		gravity = collision = !Game.debug;
		xDir = 0;
		zDir = 0;
		int dx = Mouse.getDX(), dy = Mouse.getDY();
		if(dx != 0 || dy != 0) {
			rot.x -= dy * Camera.sensitivity;
			if(rot.x > 85f)
				rot.x = 85f;
			else if(rot.x < -85f)
				rot.x = -85f;
			rot.y += dx * Camera.sensitivity;
			calcDir();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			speed = 0.02f;
		else
			speed = 0.01f;
		if(Keyboard.isKeyDown(Keyboard.KEY_Z))
			zDir += speed;
		if(Keyboard.isKeyDown(Keyboard.KEY_S))
			zDir -= speed;
		if(Keyboard.isKeyDown(Keyboard.KEY_Q))
			xDir -= speed;
		if(Keyboard.isKeyDown(Keyboard.KEY_D))
			xDir += speed;
		if(Game.debug) {
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
				ya += speed;
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				ya -= speed;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && grounded)
			ya += 0.23f;
		xa += xDir * Math.sin(Math.toRadians(rot.y + 90)) + zDir * Math.sin(Math.toRadians(rot.y));
		za -= zDir * Math.cos(Math.toRadians(rot.y)) + xDir * Math.cos(Math.toRadians(rot.y + 90));
		ya = move(xa, ya, za);
		count ++;
		if(count == 5) {
			count = 0;
			Client.send(new UpdatePosPack(id, pos, rot));
		}
		xa *= 0.9f;
		ya *= 0.96f;
		za *= 0.9f;
		int wheel = Mouse.getDWheel();
		if(weapons.get(wIndex).getState() == Weapon.OFF && weapons.get(wIndex).getNextState() == Weapon.OFF ) {
			wIndex += wUp ? 1 : -1;
			if(wIndex < 0)
				wIndex = (byte) (weapons.size()-1);
			else if(wIndex >= weapons.size())
				wIndex = 0;
			Client.send(new WeaponChangePack(id, wIndex));
			weapons.get(wIndex).setState(Weapon.IDLE, true);
		} else if(wheel > 0) {
			wUp = true;
			weapons.get(wIndex).setState(Weapon.OFF, true);
		} else if(wheel < 0) {
			wUp = false;
			weapons.get(wIndex).setState(Weapon.OFF, true);
		}
		if(Input.getMouseDown(1))
			weapons.get(wIndex).setState(Weapon.VISE, true);
		if(Input.getMouseUp(1))
			weapons.get(wIndex).setState(Weapon.IDLE, true);
		if(Mouse.isButtonDown(0)) {
			weapons.get(wIndex).shoot();
		}
		weapons.get(wIndex).update(this);
		if(showDamageEffect) {
			alpha += xAlpha - 0.025f;
			xAlpha *= 0.95f;
			if(alpha < 0)
				showDamageEffect = false;
		}
		updateChat();
		if(Game.debug)
			posText.setText(pos.x + "  " + pos.y + "  " + pos.z);
		if(Input.getKeyDown(28) && Mouse.isGrabbed()) {
			Mouse.setGrabbed(false);
			input.setFocus(true);
			chat.setVisibility(1);
		}
	}

	public void updateChat() {
		chat.update();
		input.update();
	}

	public void render() {}

	public void renderFirst() {
		weapons.get(wIndex).renderFirst();
	}

	public void renderGUI() {
		if(Game.debug)
			posText.renderGUI();
		healthText.renderGUI();
		healthBar.renderGUI();
		chat.renderGUI();
		if(!Mouse.isGrabbed())
			input.renderGUI();
		if(showDamageEffect) {
			float width = Display.getWidth(), height = Display.getHeight();
			glColor4f(1, 1, 1, alpha);
			Texture.DAMAGE_EFFECT.bind();
			glBegin(GL_QUADS);
				glTexCoord2f(0, 0);
				glVertex2f(0, 0);
				glTexCoord2f(1, 0);
				glVertex2f(width, 0);
				glTexCoord2f(1, 1);
				glVertex2f(width, height);
				glTexCoord2f(0, 1);
				glVertex2f(0, height);
			glEnd();
			Texture.DAMAGE_DIR.bind();
			glBegin(GL_QUADS);
				glTexCoord2f(0, 0);
				glVertex2f(c1.x+width/2, c1.y+height/2);
				glTexCoord2f(1, 0);
				glVertex2f(c2.x+width/2, c2.y+height/2);
				glTexCoord2f(1, 1);
				glVertex2f(c3.x+width/2, c3.y+height/2);
				glTexCoord2f(0, 1);
				glVertex2f(c4.x+width/2, c4.y+height/2);
			glEnd();
			glColor4f(1, 1, 1, 1);
			Texture.unbind();
		}
	}

	public void displayResized() {
		healthBar.anchorUpdate();
		healthText.anchorUpdate();
		chat.anchorUpdate();
		input.anchorUpdate();
	}

	public Vec3 getEyesPos() {
		Vec3 res = pos.copy();
		res.y += eyesHeight;
		return res;
	}

	public void addDamage(float damage, Vec3 dir) {
		health -= damage;
		showDamageEffect = true;
		xAlpha *= 0.4f;
		xAlpha += 0.00125f*damage + 0.05f;
		if(health <= 0) {
			health = 100;
			pos = Game.instance.getSpawnPos().copy();
			xAlpha = alpha = 0;
			showDamageEffect = false;
		}
		healthBar.setProgression(health/100);
		healthText.setText(((int) health) + "/100");
		Vec2 dir1 = new Vec2(this.dir.x, this.dir.z).normalized(), dir2 = new Vec2(dir.x, dir.z).normalized();
		float cos = - dir1.cross(dir2), sin = - dir1.dot(dir2);
		float circleRad = ((float) Math.min(Display.getHeight(), Display.getWidth()))/2.0f;
		c1.set(0.85f*circleRad*cos - 0.222f*circleRad*sin, - 0.85f*circleRad*sin - 0.222f*circleRad*cos);
		c2.set(0.85f*circleRad*cos + 0.222f*circleRad*sin, - 0.85f*circleRad*sin + 0.222f*circleRad*cos);
		c3.set(0.6f*circleRad*cos + 0.222f*circleRad*sin, - 0.6f*circleRad*sin + 0.222f*circleRad*cos);
		c4.set(0.6f*circleRad*cos - 0.222f*circleRad*sin, - 0.6f*circleRad*sin - 0.222f*circleRad*cos);
	}

	public void msg(String name, String text) {
		chat.addLine(GuiArea.messC, new String[] {name + " : ", text});
	}

	public boolean isMouseInChat() {
		int mx = Mouse.getX(), my = Mouse.getY();
		return mx >= 8 && mx <= 238 && my >= 50 && my <= 201;
	}

	public void stopFocus() {
		input.setFocus(false);
		chat.setVisibility(0.2f);
	}

	public float getVelocity() { return (float) Math.sqrt(xa*xa + za*za); }

	public float getXa() { return xa; }
	public float getYa() { return ya; }
	public float getZa() { return za; }

}
