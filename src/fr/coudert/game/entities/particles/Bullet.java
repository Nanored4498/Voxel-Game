package fr.coudert.game.entities.particles;

import fr.coudert.game.entities.player.Player;
import fr.coudert.game.objects.*;
import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.network.Client;
import fr.coudert.network.packets.HitPack;
import fr.coudert.rendering.Color;
import fr.coudert.rendering.FrustumCulling;

public class Bullet extends Particle {

	private static final Color COLOR = new Color(0.1f, 0.1f, 0.1f);
	private static final int LIFE_TIME = 90;

	private boolean enemy;
	private byte count;
	private Color color;
	private float damage, attenuation;
	
	public Bullet(int id, Vec3 pos, Vec2 rot, float speed, float r, int playerID, byte weapon) {
		super(id, pos, rot, speed, r, COLOR);
		enemy = playerID != Player.localPlayer.id;
		count = -1;
		switch(weapon) {
		case Weapon.GUN:
			damage = Gun.DAMAGE;
			attenuation = Gun.ATTENUATION;
			break;
		case Weapon.AK47:
			damage = Ak47.DAMAGE;
			attenuation = Ak47.ATTENUATION;
			break;
		case Weapon.SNIPER:
			damage = Sniper.DAMAGE;
			attenuation = Sniper.ATTENUATION;
			break;
		}
	}

	public void update() {
		if(count > 0) {
			count ++;
			if(count == 5 && color != null)
				Game.instance.getEntitiesManager().add(new ParticleSystem(id, pos.sub(dir.mul(0.1f)), dir.normalized().mul(-0.09f), 30, 130, color.copy().mul(0.65f)));
			else if(count == LIFE_TIME)
				destroyed = true;
			return;
		} else {
			count --;
			if(count == -LIFE_TIME)
				count = LIFE_TIME-1;
		}
		color = move2(dir, enemy);
		isInViewFrustum = FrustumCulling.isInViewFrustum(pos, r*1.732f);
		if(color != null) {
			if(color == Color.BLOOD) {
				Client.send(new HitPack(id, Player.localPlayer.id, pos));
				Player.localPlayer.addDamage(damage, dir);
				Game.instance.getEntitiesManager().add(new ParticleSystem(id, pos, dir.normalized().mul(0.09f), 30, 100, color));
			} else
				count = 1;
		}
		damage *= attenuation;
	}

}