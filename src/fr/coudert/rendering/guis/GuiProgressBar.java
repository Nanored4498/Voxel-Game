package fr.coudert.rendering.guis;

import static org.lwjgl.opengl.GL11.*;

public class GuiProgressBar extends GuiComponent {

	private float progression;
	private float x1, y1, w1, h1;

	public GuiProgressBar(int x, int y, int w, int h) {
		super(x, y, w, h);
		progression = 0f;
		x1 = x+2;
		y1 = y+2;
		h1 = h-4;
	}

	public void update() {
		
	}

	public void renderGUI() {
		glColor4f(0.85f, 0.2f, 0.2f, 1.0f);
		Gui.renderQuad(x1, y1, w1, h1);
		glColor4f(0.2f, 0.2f, 0.25f, 1.0f);
		Gui.renderQuad(x, y, w, h);
	}

	public void setProgression(float progress) {
		progression = progress;
		w1 = (w-4)*progression;
	}

	public void anchorUpdate() {
		anchorUpdateMain();
		x1 = x+2;
		y1 = y+2;
	}

}