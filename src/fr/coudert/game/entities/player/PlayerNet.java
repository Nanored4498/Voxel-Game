package fr.coudert.game.entities.player;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import fr.coudert.game.entities.Entity;
import fr.coudert.game.objects.*;
import fr.coudert.game.world.blocks.Block;
import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;
import fr.coudert.rendering.FrustumCulling;
import fr.coudert.rendering.Shader;

public class PlayerNet extends Entity {

	private static int vbo;

	private boolean isInViewFrustum;
	private Vec3 newPos;
	private Vec2 newRot;
	private float count;
	private ArrayList<Weapon> weapons;
	private byte wIndex;

	public PlayerNet(int id, Vec3 pos, Vec2 rot, byte wIndex, byte wState) {
		super(id, pos, rot, 10.0f, 0.3f);
		height = 1.25f;
		newPos = pos;
		newRot = rot;
		weapons = new ArrayList<Weapon>();
		weapons.add(new Gun());
		weapons.add(new Ak47());
		weapons.add(new Sniper());
		this.wIndex = wIndex;
		weapons.get(wIndex).setState(wState, false);
	}

	public static void initVBO() {
		vbo = glGenBuffers();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(Block.DATA_SIZE);
		buffer.put(new float[] {
				-0.3f, -1.25f, -0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				0.3f, -1.25f, -0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				0.3f, 1.25f, -0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				-0.3f, 1.25f, -0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
		
				-0.3f, -1.25f, 0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				-0.3f, 1.25f, 0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				0.3f, 1.25f, 0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				0.3f, -1.25f, 0.3f, 0.18f, 0.18f, 0.18f, 1.0f,
				
				-0.3f, -1.25f, -0.3f, 0.14f, 0.14f, 0.14f, 1.0f,
				-0.3f, -1.25f, 0.3f, 0.14f, 0.14f, 0.14f, 1.0f,
				0.3f, -1.25f, 0.3f, 0.14f, 0.14f, 0.14f, 1.0f,
				0.3f, -1.25f, -0.3f, 0.14f, 0.14f, 0.14f, 1.0f,
				
				-0.3f, 1.25f, -0.3f, 0.2f, 0.2f, 0.2f, 1.0f,
				0.3f, 1.25f, -0.3f, 0.2f, 0.2f, 0.2f, 1.0f,
				0.3f, 1.25f, 0.3f, 0.2f, 0.2f, 0.2f, 1.0f,
				-0.3f, 1.25f, 0.3f, 0.2f, 0.2f, 0.2f, 1.0f,
				
				-0.3f, -1.25f, -0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				-0.3f, 1.25f, -0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				-0.3f, 1.25f, 0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				-0.3f, -1.25f, 0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				
				0.3f, -1.25f, -0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				0.3f, -1.25f, 0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				0.3f, 1.25f, 0.3f, 0.16f, 0.16f, 0.16f, 1.0f,
				0.3f, 1.25f, -0.3f, 0.16f, 0.16f, 0.16f, 1.0f
		});
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	public void update() {
		isInViewFrustum = FrustumCulling.isInViewFrustum(pos, r+height);
		float temp = (float) Math.cos(1.0f / count);
		pos.lerp(newPos, temp);
		rot.lerp(newRot, temp);
		count ++;
		weapons.get(wIndex).update();
	}

	public void render() {
		if(!isInViewFrustum)
			return;
		Shader.MAIN.bind();
		glTranslatef(pos.x, pos.y, pos.z);
		glRotatef(-rot.y, 0, 1, 0);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * 4, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * 4, 12);
		glDrawArrays(GL_QUADS, 0, Block.DATA_SIZE);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glRotatef(-rot.x, 1, 0, 0);
		weapons.get(wIndex).render();
		glRotatef(rot.x, 1, 0, 0);
		glRotatef(rot.y, 0, 1, 0);
		glTranslatef(-pos.x, -pos.y, -pos.z);
		Shader.unBind();
	}

	public void updatePos(Vec3 newPos, Vec2 newRot) {
		pos = this.newPos;
		rot = this.newRot;
		this.newPos = newPos;
		this.newRot = newRot;
		count = 1;
	}

	public void setWeaponIndex(byte id) { wIndex = id; }
	public void setWeaponState(byte id, byte state) { weapons.get(id).setState(state, false); }

}