package fr.coudert.game.objects;

import static org.lwjgl.opengl.GL11.*;

import fr.coudert.game.entities.player.Player;
import fr.coudert.game.models.Model;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Client;
import fr.coudert.network.packets.*;
import fr.coudert.rendering.Camera;

public abstract class Weapon {

	//TODO: Petits déplacements de l'arme lorsque l'on court ou lorsqu l'on bouge la caméra

	public static final byte IDLE = 0, VISE = 1, OFF = 2;
	public static final byte GUN = 0, AK47 = 1, SNIPER = 2;

	protected Model model;
	protected Vec3 pos, pos2;
	protected Vec3[] states;
	protected boolean trans;
	protected float transValue;
	protected byte state, nextState;
	protected int reShootTime, shootTime, shootTick;
	protected float shootForce;
	protected boolean isShooting;
	protected float scopePrecision;
	protected byte id;
	protected float phase;

	protected Weapon(byte id, Model model, Vec3 idle, Vec3 vise, int reShootTime, int shootTime, float shootForce, float scopePrecision) {
		this.id = id;
		this.model = model;
		this.reShootTime = reShootTime;
		this.shootTime = shootTime;
		this.shootForce = shootForce;
		this.scopePrecision = scopePrecision;
		states = new Vec3[] {idle, vise, new Vec3(-0.6f, -1, -0.2f)};
		trans = false;
		state = OFF;
		pos = pos2 = states[state];
		transValue = 1;
		shootTick = 0;
	}

	public void setState(byte state, boolean send) {
		if((this.state == OFF || nextState == OFF) && transValue != 1)
			return;
		if(trans) {
			this.state = nextState;
			nextState = state;
			transValue = 1 - transValue;
		} else {
			nextState = state;
			trans = true;
			transValue = 0;
		}
		if(send)
			Client.send(new WeaponStatePack(Player.localPlayer.id, id, state));
	}

	public void shoot() {
		if(!isShooting && nextState != OFF) {
			isShooting = true;
			Client.send(new ShootPack(Player.localPlayer.id));
		}
	}

	public void update(Player player) {
		if(trans) {
			transValue += 1/17f;
			if(nextState == VISE)
				Camera.fov = 70 - scopePrecision*transValue;
			else if(state == VISE)
				Camera.fov = 70 - scopePrecision*(1-transValue);
			else
				Camera.fov = 70;
			Camera.sensitivity = .01f * Camera.fov * Camera.baseSensitivity;
			if(transValue >= 1) {
				transValue = 1;
				trans = false;
				state = nextState;
			}
			pos2 = pos = states[state].copy().lerp(states[nextState], transValue);
		}
		if(isShooting) {
			shootTick ++;
			if(shootTick <= shootTime) {
				pos2 = pos.copy();
				int tier = shootTime/3;
				int t = Math.min(shootTick, tier);
				pos2.x -= shootForce/100 * t * (-t/3 + (tier+1)/2);
				if(shootTick <= tier)
					player.rot.x -= (shootForce + 4)/5*(shootTime-shootTick)/shootTime;
				else if(shootTick <= 2*tier || player.rot.x < -85f)
					player.rot.x += 0.75f;
				float t2 = (float)Math.max(0, shootTick - tier)/(float)(shootTime-tier);
				pos2.lerp(pos, t2);
			}
			if(shootTick == reShootTime) {
				isShooting = false;
				shootTick = 0;
			}
		}
	}

	public void update() {
		if(trans) {
			transValue += 1/17f;
			if(transValue >= 1) {
				transValue = 1;
				trans = false;
				state = nextState;
			}
			pos = pos2 = states[state].copy().lerp(states[nextState], transValue);
		}
		if(isShooting) {
			shootTick ++;
			if(shootTick <= shootTime) {
				pos2 = pos.copy();
				int tier = shootTime/3;
				int t = Math.min(shootTick, tier);
				pos2.x -= shootForce/100 * t * (-t/3 + (tier+1)/2);
				float t2 = (float)Math.max(0, shootTick - tier)/(float)(shootTime-tier);
				pos2.lerp(pos, t2);
			}
			if(shootTick == reShootTime) {
				isShooting = false;
				shootTick = 0;
			}
		}
	}
	
	public void renderFirst() {
		glDisable(GL_DEPTH_TEST);
		glPushMatrix();
		glRotatef(90f, 0, 1, 0);
		model.render(pos2.x, pos2.y, pos2.z);
		glRotatef(-90f, 0, 1, 0);
		glPopMatrix();
		glEnable(GL_DEPTH_TEST);
	}

	public void render() {
		glRotatef(90f, 0, 1, 0);
		model.render(pos2.x, pos2.y + 1.0f, pos2.z);
		glRotatef(-90f, 0, 1, 0);
	}

	public int getState() { return state; }
	public int getNextState() { return nextState; }

}