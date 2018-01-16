package fr.coudert.game.objects;

import fr.coudert.game.models.Model;
import fr.coudert.maths.Vec3;

public class Ak47 extends Weapon {

	public static final float DAMAGE = 14.0f, ATTENUATION = 0.95f;

	public Ak47() {
		super(Weapon.AK47, Model.AK47, new Vec3(0.45f, -0.65f, 0f), new Vec3(0.5f, -0.565f, -0.47f), 15, 10, 1.18f, 13);
	}

}