package fr.coudert.rendering.guis;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.utils.Input;

public abstract class GuiSlider extends GuiComponent {

	protected float value;
	private int lineX, lineY, lineW, lineH;
	private int cursorX, cursorW;
	private int maxX;
	private float cursorRed;
	private boolean isGrabbed;
	private int dx;

	public GuiSlider(int x, int y, int w, int h) {
		super(x, y, w, h);
		value = 0;
		lineX = x + w / 26;
		lineY = y + h / 3;
		lineW = w * 12 / 13;
		lineH = h / 3;
		cursorX = x;
		cursorW = w / 13;
		maxX = x + lineW;
		cursorRed = 0.5f;
		isGrabbed = false;
	}

	public void update() {
		int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
		if(isGrabbed || (mx <= cursorX + cursorW && mx >= cursorX && my <= y + h && my >= y)) {
			cursorRed = 0.8f;
			if(Input.getMouseDown(0)) {
				isGrabbed = true;
				dx = cursorX - mx;
			}
		} else
			cursorRed = 0.45f;
		if(isGrabbed && Mouse.isButtonDown(0)) {
			cursorX = mx + dx;
			if(cursorX < x)
				cursorX = x;
			if(cursorX > maxX)
				cursorX = maxX;
			value = (float) (cursorX - x) / lineW;
			cursorRed = 1;
			onMove();
		} else
			isGrabbed = false;
	}

	protected abstract void onMove();

	public void renderGUI() {
		Gui.color(cursorRed, 0.45f, 0.45f, 1);
		Gui.renderQuad(cursorX, y, cursorW, h);
		Gui.color(1, 1, 1, 0.9f);
		Gui.renderQuad(lineX, lineY, lineW, lineH);
	}

	public void anchorUpdate() {
		anchorUpdateMain();
		lineX = x + w / 26;
		lineY = y + h / 3;
		cursorX = (int) (value * lineW + x);
		maxX = x + lineW;
	}

	public void setValue(float value) {
		this.value = value;
		cursorX = (int) (value * lineW + x);
	}

	public float getValue() { return value; }

}