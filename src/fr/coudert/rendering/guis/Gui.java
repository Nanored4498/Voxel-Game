package fr.coudert.rendering.guis;

import static org.lwjgl.opengl.GL11.*;
import fr.coudert.rendering.Color;
import fr.coudert.rendering.Texture;

public class Gui {

	private static Texture font = new Texture("font.png");
	private static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.;,:=+-*/\\()!?@ ";

	public static void drawString(String s, int x, int y, int size, boolean center) {
		s = s.toUpperCase();
		int charSize = (int) (size * 9f / 10f);
		if(center)
			x -= s.length() * charSize / 2;
		font.bind();
		glBegin(GL_QUADS);
		for(byte b = 0; b < s.length(); b++)
			drawChar(s.charAt(b), x + b * charSize, y, size);
		glEnd();
		Texture.unbind();
	}

	public static void drawChar(char c, int x, int y, int size) {
		int index = chars.indexOf(c);
		float x0 = (index % 26) / 26f;
		float y0 = (index / 26) / 6f;
		float x1 = (index % 26 + 1) / 26f;
		float y1 = (index / 26 + 1) / 6f;
		glTexCoord2f(x0, y0);	glVertex2i(x, y);
		glTexCoord2f(x1, y0);	glVertex2i(x + size, y);
		glTexCoord2f(x1, y1);	glVertex2i(x + size, y + size);
		glTexCoord2f(x0, y1);	glVertex2i(x, y + size);
	}

	public static void renderQuad(float x, float y, float w, float h) {
		glBegin(GL_QUADS);
			glVertex2f(x, y);
			glVertex2f(x + w, y);
			glVertex2f(x + w, y + h);
			glVertex2f(x, y + h);
		glEnd();
	}

	public static void color(float r, float g, float b, float a) {
		glColor4f(r, g, b, a);
	}

	public static void color(Color color) {
		glColor4f(color.r, color.g, color.b, color.a);
	}

}