package fr.coudert.game.entities.particles;

import java.util.Random;

import fr.coudert.game.entities.Entity;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.Color;

public class ParticleSystem extends Entity {

	private int lifeTime, time, num;
	private Particle[] particles;

	public ParticleSystem(int id, Vec3 pos, Vec3 dir, int num, int lifeTime, Color color) {
		super(id, pos, 0, 0);
		this.dir = dir;
		this.lifeTime = lifeTime;
		this.num = num;
		time = 0;
		particles = new Particle[num];
		float len = dir.length();
		Random rand = new Random();
		for(int i = 0; i < num; i++)
			particles[i] = new Particle(0, pos.copy(),
					dir.copy().add(new Vec3(rand.nextFloat()*0.5f-0.25f, rand.nextFloat()*0.6f-0.1f, rand.nextFloat()*0.5f-0.25f).mul(len)),
					0.075f, color, 4.0f);
		}

	public void update() {
		time ++;
		if(time > lifeTime) {
			num --;
			if(num == 0) {
				destroyed = true;
				particles = null;
			}
		}
		for(int i = 0; i < num; i++)
			particles[i].update();
	}

	public void render() {
		for(int i = 0; i < num; i++)
			particles[i].render();
	}

}