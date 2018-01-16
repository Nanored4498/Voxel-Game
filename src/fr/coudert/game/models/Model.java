package fr.coudert.game.models;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import fr.coudert.editor.FileManager;
import fr.coudert.game.world.blocks.Block;
import fr.coudert.rendering.Color;

public class Model {

	public static final int SIZE = 30;
	public static final float SCALE = 1f/16f;
	public static final Model AK47 = new Model("ak47.vox");
	public static final Model GUN = new Model("gun.vox");
	public static final Model SNIPER = new Model("sniper.vox");
	public static final Model SHOVEL = new Model("shovel.vox");

	private static FloatBuffer buffer;

	private int vbo, bufferSize;

	//TODO: Revoir la facon dont on ajoute les faces aux buffer car on pourrait draw des plus grandes faces

	public Model(String path) {
		String text = FileManager.readFile("./res/mod/" + path), word = "";
		char a = 'a';
		int b = 0;
		int[] infos = new int[3];
		Color[][][] colors = new Color[SIZE][SIZE][SIZE];
		ArrayList<Color> addedBlocks = new ArrayList<Color>();
		for(int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if(a == 'a') {
				a = c;
			}
			else {
				if(c == '\n') {
					if(a == 'i')
						addedBlocks.add(new Color(Long.valueOf(word)));
					else
						colors[infos[1]-24][infos[2]][Integer.valueOf(word)-24] = addedBlocks.get(infos[0]);
					a = 'a';
					b = 0;
					word = "";
				} else if(c == ',') {
					infos[b] = Integer.valueOf(word);
					b ++;
					word = "";
				} else
					word += c;
			}
		}
		vbo = glGenBuffers();
		bufferSize = 0;
		if(buffer == null)
			buffer = BufferUtils.createFloatBuffer(SIZE * SIZE * SIZE * Block.DATA_SIZE / 4);
		buffer.clear();
		float S = 1f/16f;
		for(byte i = SIZE-1; i >= 0; i--)
			for(byte j = 0; j < SIZE; j++)
				for(byte k = 0; k < SIZE; k++) {
					Color color = colors[i][j][k];
					if(color == null)
						continue;
					float x = i/16f, y = j/16f, z = k/16f;
					boolean up = isNull(i, (byte) (j+1), k, colors);
					boolean down = isNull(i, (byte) (j-1), k, colors);
					boolean left = isNull((byte) (i-1), j, k, colors);
					boolean rigth = isNull((byte) (i+1), j, k, colors);
					boolean front = isNull(i, j, (byte) (k-1), colors);
					boolean back = isNull(i, j, (byte) (k+1), colors);
					if(back) {
						buffer.put(new float[] {
							x, y, z + S,			color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x, y + S, z + S,		color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x + S, y + S, z + S,	color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x + S, y, z + S,		color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a
						});
						bufferSize += 4;
					}
					if(down) {
						buffer.put(new float[] {
							x, y, z,				color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a,
							x, y, z + S,			color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a,
							x + S, y, z + S,		color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a,
							x + S, y, z,			color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a
						});
						bufferSize += 4;
					}
					if(rigth) {
						buffer.put(new float[] {
							x + S, y, z,			color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x + S, y, z + S,		color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x + S, y + S, z + S,	color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x + S, y + S, z,		color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a
						});
						bufferSize += 4;
					}
					if(up) {
						buffer.put(new float[] {
							x, y + S, z,			color.r, color.g, color.b, color.a,
							x + S, y + S, z,		color.r, color.g, color.b, color.a,
							x + S, y + S, z + S,	color.r, color.g, color.b, color.a,
							x, y + S, z + S,		color.r, color.g, color.b, color.a
						});
						bufferSize += 4;
					}
					if(left) {
						buffer.put(new float[] {
							x, y, z,				color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x, y + S, z,			color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x, y + S, z + S,		color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a,
							x, y, z + S,			color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a
						});
						bufferSize += 4;
					}
					if(front) {
						buffer.put(new float[] {
							x, y, z,				color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x + S, y, z,			color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x + S, y + S, z,		color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a,
							x, y + S, z,			color.r * 0.9f, color.g * 0.9f, color.b * 0.9f, color.a
						});
						bufferSize += 4;
					}
				}
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	private boolean isNull(byte x, byte y, byte z, Color[][][] colors) {
		if(x >= SIZE || y >= SIZE || z >= SIZE || x == -1 || y == -1 || z == -1)
			return true;
		return colors[x][y][z] == null;
	}

	public void render(float x, float y, float z) {
		glTranslatef(x, y, z);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * 4, 0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * 4, 12);
		glDrawArrays(GL_QUADS, 0, bufferSize);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glTranslatef(-x, -y, -z);
	}

}