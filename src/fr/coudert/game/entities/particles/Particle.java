package fr.coudert.game.entities.particles;

import static org.lwjgl.opengl.GL11.*;

import fr.coudert.game.entities.Entity;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.*;
import fr.coudert.rendering.guis.Gui;

public class Particle extends Entity {

	protected Color color;
	protected boolean isInViewFrustum;

	public Particle(int id, Vec3 pos, Vec2 rot, float speed, float r, Color color) {
		super(id, pos, rot, 0.2f, r);
		calcDir();
		dir.mul(speed);
		this.color = color;
		isInViewFrustum = false;
	}

	public Particle(int id, Vec3 pos, Vec3 dir, float r, Color color, float mass) {
		super(id, pos, mass, r);
		this.dir = dir;
		this.color = color;
		isInViewFrustum = false;
	}

	public void update() {
		move2(dir, false);
		isInViewFrustum = FrustumCulling.isInViewFrustum(pos, r*1.732f);
	}

	//TODO: Passer le rendu en VBO
	public void render() {
		if(!isInViewFrustum)
			return;
		Gui.color(color);
		glBegin(GL_QUADS);
			glVertex3f(pos.x - r, pos.y - r, pos.z - r);
			glVertex3f(pos.x + r, pos.y - r, pos.z - r);
			glVertex3f(pos.x + r, pos.y + r, pos.z - r);
			glVertex3f(pos.x - r, pos.y + r, pos.z - r);
	
			glVertex3f(pos.x - r, pos.y - r, pos.z + r);
			glVertex3f(pos.x - r, pos.y + r, pos.z + r);
			glVertex3f(pos.x + r, pos.y + r, pos.z + r);
			glVertex3f(pos.x + r, pos.y - r, pos.z + r);
			
			glVertex3f(pos.x - r, pos.y - r, pos.z - r);
			glVertex3f(pos.x - r, pos.y - r, pos.z + r);
			glVertex3f(pos.x + r, pos.y - r, pos.z + r);
			glVertex3f(pos.x + r, pos.y - r, pos.z - r);
			
			glVertex3f(pos.x - r, pos.y + r, pos.z - r);
			glVertex3f(pos.x + r, pos.y + r, pos.z - r);
			glVertex3f(pos.x + r, pos.y + r, pos.z + r);
			glVertex3f(pos.x - r, pos.y + r, pos.z + r);
			
			glVertex3f(pos.x - r, pos.y - r, pos.z - r);
			glVertex3f(pos.x - r, pos.y + r, pos.z - r);
			glVertex3f(pos.x - r, pos.y + r, pos.z + r);
			glVertex3f(pos.x - r, pos.y - r, pos.z + r);
			
			glVertex3f(pos.x + r, pos.y - r, pos.z - r);
			glVertex3f(pos.x + r, pos.y - r, pos.z + r);
			glVertex3f(pos.x + r, pos.y + r, pos.z + r);
			glVertex3f(pos.x + r, pos.y + r, pos.z - r);
		glEnd();
	}

}