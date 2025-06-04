package fr.coudert.rendering.guis;

import java.awt.Font;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.utils.Input;

public abstract class GuiInputField extends GuiComponent {

	private GuiText text;
	private int tick, tick2;
	private boolean drawC, focus;
	private int cx, cy, ch;
	private String past;

	public GuiInputField(int x, int y, int w, int h) {
		super(x, y, w, h);
		text = new GuiText(new TrueTypeFont(new Font("Arial", Font.PLAIN, (int) ((h-4)/1.16f)), true), "", x+3, y+1);
		tick = tick2 = 0;
		drawC = true;
		focus = false;
		cx = x+4;
		cy = y + 4;
		ch = h - 8;
		past = "";
	}

	public void update() {
		int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
		if(Input.getMouseDown(0))
			focus = mx >= x && mx <= x+w && my >= y && my <= y+h;
		if(focus) {
			tick ++;
			if(tick == 15) {
				tick = 0;
				drawC = !drawC;
			}
			for(int i = 14; i <= 82; i++)
				if(Input.getKeyDown(i)) {
					final char c = Input.getKeyChar(i, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
					if(c == 0) continue;
					String newT = text.getText() + c;
					while(text.getFont().getWidth(newT) > w - 8) {
						past += newT.charAt(0);
						newT = newT.substring(1);
					}
					text.setText(newT);
					cx = text.x + text.w;
				}
			if(Keyboard.isKeyDown(14)) {
				if(((tick2 > 14 && tick % 2 == 0) || Input.getKeyDown(14)) && text.getText().length() > 0) {
					String s = text.getText();
					s = s.substring(0, s.length()-1);
					while(past.length() > 0 && text.getFont().getWidth(s + past.charAt(past.length()-1)) <= w - 8) {
						s = past.charAt(past.length()-1) + s;
						past = past.substring(0, past.length()-1);
					}
					text.setText(s);
					cx = text.x + text.w;
				}
				tick2 ++;
			} else
				tick2 = 0;
			if(Input.getKeyDown(28)) {
				enter();
				past = "";
				text.setText("");
				cx = x + 4;
			}
		}
	}

	public void renderGUI() {
		text.renderGUI();
		if(focus && drawC) Gui.renderQuad(cx, cy, 2, ch);
		Gui.color(0.2f, 0.2f, 0.2f, 0.8f);
		Gui.renderQuad(x+2, y+2, w-4, h-4);
		Gui.color(0.6f, 0.6f, 0.6f, 0.5f);
		Gui.renderQuad(x, y, w, h);
	}

	public void anchorUpdate() {
		int cxp = cx - x;
		anchorUpdateMain();
		cy = y + 4;
		cx = x + cxp;
		text.setPos(x+3, y+1);
	}

	public abstract void enter();

	protected String getText() {
		return past + text.getText();
	}

	public void setFocus(boolean focus) { this.focus = focus; }

}