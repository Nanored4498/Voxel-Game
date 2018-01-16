package fr.coudert.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import fr.coudert.maths.Vec3;

public class SkyBox {

	public static SkyBox SKY_1 = new SkyBox(Texture.SKYBOX);
	
	private static int vbo = glGenBuffers();
	private Texture texture;

	public SkyBox(Texture texture) {
		this.texture = texture;
		updateVBO();
	}

	public static void updateVBO() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(5 * 4 * 6);
		buffer.put(blockData()).flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	public void render(Vec3 pos) {
		texture.bind();
		glPushMatrix();
		glTranslatef(pos.x, pos.y, pos.z);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 12);
		glDrawArrays(GL_QUADS, 0, 24);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glPopMatrix();
		Texture.unbind();
	}

	private static float[] blockData() {
		int size = (int)(Camera.far / Math.sqrt(3));
		return new float[]{
				-size, -size, -size, 0, 1, //back
				-size, size, -size, 0, 0,
				size, size, -size, 1f/6f, 0,
				size, -size, -size, 1f/6f, 1,
				
				-size, -size, -size, 1f/6f, 1, //bot
				size, -size, -size, 1f/6f, 0,
				size, -size, size, 2f/6f, 0,
				-size, -size, size, 2f/6f, 1,
				
				size, -size, -size, 3f/6f, 1, //left
				size, size, -size, 3f/6f, 0,
				size, size, size, 4f/6f, 0,
				size, -size, size, 4f/6f, 1,
				
				-size, -size, size, 3f/6f, 1, // front
				size, -size, size, 2f/6f, 1,
				size, size, size, 2f/6f, 0,
				-size, size, size, 3f/6f, 0,
				
				-size, -size, -size, 5f/6f, 1, //right
				-size, -size, size, 4f/6f, 1,
				-size, size, size, 4f/6f, 0,
				-size, size, -size, 5f/6f, 0,
				
				-size, size, -size, 6f/6f, 0, //top
				-size, size, size, 6f/6f, 1,
				size, size, size, 5f/6f, 1,
				size, size, -size, 5f/6f, 0
		};
	}

}