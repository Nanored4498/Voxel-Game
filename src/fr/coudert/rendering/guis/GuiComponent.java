package fr.coudert.rendering.guis;

import org.lwjgl.opengl.Display;

public abstract class GuiComponent {

	public static final byte TR = 1, BL = 2, C = 4;

	protected int x, y, w, h;

	private byte corner;
	private int anchorX, anchorY;

	protected GuiComponent(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public abstract void update();
	public abstract void renderGUI();

	public GuiComponent anchor(byte corner) {
		anchorMain(corner);
		return this;
	}

	protected void anchorMain(byte corner) {
		this.corner = corner;
		anchorX = x;
		anchorY = y;
		anchorUpdate();
	}

	public void anchorUpdate() {
		anchorUpdateMain();
	}

	protected void anchorUpdateMain() {
		switch(corner) {
		case TR:
			x = Display.getWidth() - anchorX - w;
			break;
		case BL:
			y = Display.getHeight() - anchorY - h;
			break;
		case C:
			x = (Display.getWidth()-w)/2 + anchorX;
			y = (Display.getHeight()-h)/2 + anchorY;
		default:
			return;
		}
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

}