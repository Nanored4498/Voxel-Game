package fr.coudert.rendering.guis;

import java.awt.Font;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.rendering.Color;
import fr.coudert.utils.Input;

public abstract class GuiButton extends GuiComponent {
	
	private static final TrueTypeFont TEST_FONT = new TrueTypeFont(new Font("Arial", Font.PLAIN, 10), false);

	private boolean startCliked;
	private GuiText text;
	private Color c1, c2, color;
	private TrueTypeFont font;

	public GuiButton(String text, int x, int y, int w, int h) {
		super(x, y, w, h);
		font = new TrueTypeFont(new Font("Arial", Font.PLAIN, (int) Math.min(h / 1.16f, 10 * w / TEST_FONT.getWidth(text))), true);
		this.text = new GuiText(font, text, x + (w-font.getWidth(text))/2, y + (h-font.getHeight()));
		color = c1 = new Color(0.6f, 0.6f, 0.6f, 0.95f);
		c2 = new Color(0.95f, 0.1f, 0.1f, 0.98f);
	}

	public abstract void onClick();

	public void update() {
		int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
		if((mx <= x + w && mx >= x && my <= y + h && my >= y)) {
			color = c2;
			if(Input.getMouseDown(0))
				startCliked = true;
			else if(Input.getMouseUp(0) && startCliked)
				onClick();
			else if(!Mouse.isButtonDown(0)) {
				color = c1.interpolate(c2, 0.5f);
				startCliked = false;
			}
		} else {
			color = c1;
			startCliked = false;
		}
	}

	public void renderGUI() {
		Gui.color(1, 1, 1, 1);
		text.renderGUI();
		Gui.color(color);
		Gui.renderQuad(x, y, w, h);
	}

	public void anchorUpdate() {
		anchorUpdateMain();
		text.setPos(x + (w-font.getWidth(text.getText()))/2, y + (h-font.getHeight()));
	}

}