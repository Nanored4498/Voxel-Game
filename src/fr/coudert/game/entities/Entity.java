package fr.coudert.game.entities;

import fr.coudert.game.entities.player.Player;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.Color;

public abstract class Entity {

	public int id;
	public Vec3 pos;
	public Vec3 dir;
	public Vec2 rot;

	protected boolean gravity = true, collision = true;
	protected boolean grounded = false;
	protected float height, mass;
	protected float r;
	protected boolean destroyed;

	public Entity(int id, Vec3 pos, float mass, float r) {
		this(id, pos, new Vec2(), mass, r);
	}

	public Entity(int id, Vec3 pos, Vec2 rot, float mass, float r) {
		this.id =  id;
		this.pos = pos;
		this.rot = rot;
		dir = new Vec3();
		this.mass = mass;
		this.r = height = r;
		destroyed = false;
	}

	//TODO: Réfléchir à une optimisation des collisions
	public float move(float xa, float ya, float za) {
		if(gravity && !grounded) {
			final float SPEED_LIM = 0.8f;
			ya -= Game.instance.getWorld().GRAVITY * mass;
			if(ya < -SPEED_LIM) ya = -SPEED_LIM;
		}
		if(collision) {
			final double len = Math.sqrt(xa*xa + ya*ya + za*za);
			if(len > 0.) {
				final int DIVIDE = 50;
				int step = Math.max(1, (int) Math.abs(len * DIVIDE));
				final float mul = (float) (1. / step);
				Vec3 add = new Vec3(xa*mul, ya*mul, za*mul);
				while(--step >= 0) {
					Vec3 newPos = pos.copy().add(add);
					if(isColliding(newPos)) {
						boolean stop = true;
						newPos = pos.copy();
						if(add.x != 0.f) {
							newPos.x += add.x;
							if(isColliding(newPos)) {
								newPos.x = pos.x;
								add.x = 0.f;
							} else stop = false;
						}
						if(add.y != 0.f) {
							newPos.y += add.y;
							if(isColliding(newPos)) {
								newPos.y = pos.y;
								add.y = 0.f;
								ya = 0;
							} else stop = false;
						}
						if(add.z != 0.f) {
							newPos.z += add.z;
							if(isColliding(newPos)) {
								newPos.z = pos.z;
								add.z = 0.f;
							} else stop = false;
						}
						if(stop) break;
					}
					pos = newPos;
				}
				Vec3 below = pos.copy();
				below.y -= .025f;
				grounded = isColliding(below);
				if(grounded) ya = 0;
			}
		} else {
			pos.x += xa;
			pos.y += ya;
			pos.z += za;
		}
		return ya;
	}

	public Color move2(Vec3 a, boolean canHit) {
		a.y -= Game.instance.getWorld().GRAVITY * mass;
		Vec3 add = a.copy();
		int step = (int) Math.abs(add.length() * 60);
		add.mul(1/((float)step));
		for(int i = 0; i < step; i++) {
			if(canHit) {
				final Vec3 dpos = Player.localPlayer.pos.copy().sub(pos);
				final float rad = r + Player.localPlayer.getRadius();
				final float hei = height + Player.localPlayer.getHeight();
				if(Math.abs(dpos.x) < rad && Math.abs(dpos.y) < hei && Math.abs(dpos.z) < rad)
					return Color.BLOOD;
			}
			final Vec3 newPos = pos.copy().add(add);
			for(int x = floor(newPos.x - r); x <= floor(newPos.x + r); x++)
				for(int y = floor(newPos.y - height); y <= floor(newPos.y + height); y++)
					for(int z = floor(newPos.z - r); z <= floor(newPos.z + r); z++)
						if(Game.instance.getWorld().haveHitbox(x, y, z))
							return Game.instance.getWorld().getBlock(x, y, z).getColor();
			pos = newPos;
		}
		return null;
	}

	private boolean isColliding(Vec3 p) {
		for(int x = floor(p.x - r); x <= floor(p.x + r); x++)
			for(int y = floor(p.y - height); y <= floor(p.y + height); y++)
				for(int z = floor(p.z - r); z <= floor(p.z + r); z++)
					if(Game.instance.getWorld().haveHitbox(x, y, z))
						return true;
		return false;
	}

	private int floor(float v) {
		return (v % 1) < 0 ? (int) v - 1 : (int) v;
	}

	protected void calcDir() {
		final double rx = Math.toRadians(rot.x);
		final double ry = Math.toRadians(rot.y);
		final float cosP = (float) Math.cos(rx);
		dir = new Vec3((float) Math.sin(ry) * cosP, (float) - Math.sin(rx), (float) - Math.cos(ry) * cosP);
	}

	public Vec3 getLeft() {
		return getRight().mul(-1);
	}

	public Vec3 getRight() {
		return new Vec3((float) Math.cos(Math.toRadians(rot.y)), 0, (float) Math.sin(Math.toRadians(rot.y))).normalized();
	}

	public Vec3 getDown() {
		return dir.copy().cross(getRight()).normalized();
	}

	public Vec3 getUp() {
		return getDown().copy().mul(-1);
	}

	public abstract void update();
	public abstract void render();

	public float getHeight() { return height; }
	public float getRadius() { return r; }
	public boolean isDestroyed() { return destroyed; }
	public void destroyed() { destroyed = true; }

}