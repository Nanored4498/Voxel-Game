package fr.coudert.rendering.guis;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import fr.coudert.rendering.Texture;

public class TrueTypeFont {

	private class CharInfo {
		public int x, y, width;
	}

	private HashMap<Character, CharInfo> customChars;
	private int height;
	private CharInfo[] charInfos;
	private Texture texture;
	private int texWidth, texHeight;

	public TrueTypeFont(Font font, boolean antiAlias, char[] customChars) {
		this.customChars = new HashMap<Character, CharInfo>();
		charInfos = new CharInfo[256];
		texWidth = texHeight = 512;
		if(customChars != null && customChars.length > 0)
			texWidth *= 2;
		BufferedImage texImage = new BufferedImage(texWidth, texHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) texImage.getGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0, 0, texWidth, texHeight);
		BufferedImage fontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gFont = (Graphics2D) fontImage.getGraphics();
		if(antiAlias)
			gFont.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gFont.setFont(font);
		FontMetrics metrics = gFont.getFontMetrics();
		height = metrics.getHeight();
		if(height <= 0)
			height = 1;
		int posX = 0, posY = 0;
		int customCharsLength = customChars != null ? customChars.length : 0;
		for(int i = 0; i < 256 + customCharsLength; i++) {
			char c = i < 256 ? (char) i : customChars[i - 256];
			CharInfo info = new CharInfo();
			info.width = metrics.charWidth(c);
			if(info.width <= 0)
				info.width = 1;
			BufferedImage charImage = new BufferedImage(info.width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gChar = (Graphics2D) charImage.getGraphics();
			if(antiAlias)
				gChar.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			gChar.setFont(font);
			gChar.setColor(java.awt.Color.WHITE);
			gChar.drawString(String.valueOf(c), 0, metrics.getAscent());
			if(posX + info.width >= texWidth) {
				posX = 0;
				posY += height;
			}
			info.x = posX;
			info.y = posY;
			g.drawImage(charImage, posX, posY, null);
			posX += info.width;
			if(i < 256)
				charInfos[i] = info;
			else
				this.customChars.put(Character.valueOf((char) c), info);
			charImage = null;
		}
		texture = new Texture(texImage);
	}

	public TrueTypeFont(Font font, boolean antiAlias) {
		this(font, antiAlias, null);
	}

	public int getWidth(String text) {
		int result = 0;
		int c;
		for(int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			if(c < 256)
				result += charInfos[c].width;
			else
				result += customChars.get(Character.valueOf((char) c)).width;
		}
		return result;
	}

	public int getHeight() {
		return height;
	}

	public void drawString(float x, float y, String text) {
		texture.bind();
		int c;
		CharInfo info;
		glBegin(GL_QUADS);
		for(int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			if(c < 256)
				info = charInfos[c];
			else
				info = customChars.get(Character.valueOf((char) c));
			float x2 = x + info.width;
			quadData(x, y, x2, y + height, (float) info.x / texWidth, (float) info.y / texHeight, (float) (info.x + info.width) / texWidth, (float) (info.y + height) / texHeight);
			x = x2;
		}
		glEnd();
		Texture.unbind();
	}

	private void quadData(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2) {
		glTexCoord2f(srcX, srcY);	glVertex2f(drawX, drawY);
		glTexCoord2f(srcX2, srcY);	glVertex2f(drawX2, drawY);
		glTexCoord2f(srcX2, srcY2);	glVertex2f(drawX2, drawY2);
		glTexCoord2f(srcX, srcY2);	glVertex2f(drawX, drawY2);
	}

}