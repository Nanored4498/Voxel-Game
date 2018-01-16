package fr.coudert.game.objects;

import fr.coudert.game.models.Model;
import fr.coudert.maths.Vec3;

public class Sniper extends Weapon {

	public static final float DAMAGE = 58.0f, ATTENUATION = 0.98f;

	public Sniper() {
		super(Weapon.SNIPER, Model.SNIPER, new Vec3(0.42f, -0.58f, -0.08f), new Vec3(0.22f, -0.53f, -0.47f), 100, 38, 1.27f, 53);
	}

}