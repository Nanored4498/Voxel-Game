package fr.coudert.game.objects;

import fr.coudert.game.models.Model;
import fr.coudert.maths.Vec3;

public class Gun extends Weapon {

	public static final float DAMAGE = 35.0f, ATTENUATION = 0.91f;

	public Gun() {
		super(Weapon.GUN, Model.GUN, new Vec3(0.7f, -0.7f, 0.2f), new Vec3(0.5f, -0.625f, -0.5f), 30, 20, 3.5f, 20);
	}

}