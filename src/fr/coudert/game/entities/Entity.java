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
	protected float gravityFactor = 0;
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
		float add, result = ya;
		if(gravity) {
			if(grounded)
				gravityFactor = 0;
			else {
				gravityFactor += Game.instance.getWorld().GRAVITY * mass;
				ya -= gravityFactor;
			}
		}
		if(collision) {
			int step = (int) Math.abs(xa * 50);
			add = xa / step;
			for(int i = 0; i < step; i++)
				if(isColliding(add, 0, 0))
					break;
				else
					pos.x += add;
			step = (int) Math.abs(ya * 50);
			add = ya / step;
			for(int i = 0; i < step; i++)
				if(isColliding(0, add, 0)) {
					result = 0;
					break;
				} else
					pos.y += add;
			step = (int) Math.abs(za * 50);
			add = za / step;
			for(int i = 0; i < step; i++)
				if(isColliding(0, 0, add))
					break;
				else
					pos.z += add;
			grounded = isColliding(0, -0.025f, 0);
		} else {
			pos.x += xa;
			pos.y += ya;
			pos.z += za;
		}
		return result;
	}

	public Color move2(Vec3 a, boolean canHit) {
		a.y -= Game.instance.getWorld().GRAVITY * mass;
		Vec3 add = a.copy();
		int step = (int) Math.abs(add.length() * 60);
		add.mul(1/((float)step));
		for(int i = 0; i < step; i++) {
			if(canHit) {
				Vec3 playerPos = Player.localPlayer.pos;
				float playerRad = Player.localPlayer.getRadius();
				float playerHei = Player.localPlayer.getHeight();
				if(!(pos.x-r > playerPos.x+playerRad || pos.x+r < playerPos.x-playerRad || pos.y-height > playerPos.y+playerHei ||
						pos.y+height < playerPos.y-playerHei || pos.z-r > playerPos.z+playerRad || pos.z+r < playerPos.z-playerRad))
					return Color.BLOOD;
			}
			if(!isColliding(add.x, add.y, add.z))
				pos.add(add);
			else {
				for(int x = floor(pos.x + add.x - r); x <= floor(pos.x + add.x + r); x++)
					for(int y = floor(pos.y + add.y - height); y <= floor(pos.y + add.y + height); y++)
						for(int z = floor(pos.z + add.z - r); z <= floor(pos.z + add.z + r); z++)
							if(Game.instance.getWorld().haveHitbox(x, y, z))
								return Game.instance.getWorld().getBlock(x, y, z).getColor();
			}
		}
		return null;
	}

	private boolean isColliding(float xa, float ya, float za) {
		for(int x = floor(pos.x + xa - r); x <= floor(pos.x + xa + r); x++)
			for(int y = floor(pos.y + ya - height); y <= floor(pos.y + ya + height); y++)
				for(int z = floor(pos.z + za - r); z <= floor(pos.z + za + r); z++)
					if(Game.instance.getWorld().haveHitbox(x, y, z))
						return true;
		return false;
	}

	private int floor(float v) {
		return (v % 1) < 0 ? (int) v - 1 : (int) v;
	}

	protected void calcDir() {
		float cosP = (float) Math.cos(Math.toRadians(rot.x));
		dir = new Vec3((float) Math.cos(Math.toRadians(rot.y - 90)) * cosP, (float) Math.sin(Math.toRadians(-rot.x)), (float) Math.sin(Math.toRadians(rot.y - 90)) * cosP).normalized();
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