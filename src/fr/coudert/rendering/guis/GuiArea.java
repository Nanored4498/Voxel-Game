package fr.coudert.rendering.guis;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.coudert.rendering.Color;
import fr.coudert.utils.Input;

public class GuiArea extends GuiComponent {

	public static final Color[] infoC = new Color[] {new Color(0.8f, 0.6f, 0)};
	public static final Color[] messC = new Color[] {new Color(0.2f, 0.8f, 0.1f), Color.WHITE};

	private TrueTypeFont font;
	private ArrayList<Line> lines;
	private int numLin, start, end;
	private int cursorH, cursorY, cursorX, dy, maxY;
	private boolean isGrabbed;
	private float cursorRed;
	private float visibility;

	public GuiArea(TrueTypeFont font, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.font = font;
		numLin = h / font.getHeight();
		lines = new ArrayList<Line>();
		cursorH = h;
		cursorY = maxY = y;
		cursorX = x+w-6;
		start = end = 0;
		isGrabbed = false;
		dy = 0;
		visibility = 1;
	}

	public void update() {
		int mx = Mouse.getX(), my = Display.getHeight() - Mouse.getY();
		if(isGrabbed && Mouse.isButtonDown(0)) {
			cursorRed = 1;
			cursorMoved(my + dy);
		} else {
			isGrabbed = false;
			if(mx <= cursorX + 6) {
				if(mx >= cursorX && my <= cursorY+cursorH && my >= cursorY) {
					cursorRed = 0.8f;
					if(Input.getMouseDown(0)) {
						isGrabbed = true;
						dy = cursorY - my;
					}
				} else {
					if(mx >= x && my <= y+h && my >= y)
						cursorMoved((int) (cursorY - Input.getDWheel()*0.1f));
					cursorRed = 0.45f;
				}
			} else
				cursorRed = 0.45f;
		}
	}

	private void cursorMoved(int newPos) {
		cursorY = Math.max(y, Math.min(newPos, maxY));
		if(maxY != 0) {
			start = Math.max(((lines.size()-numLin)*cursorY) / maxY, 0);
			end = Math.min(start+numLin, lines.size());
		}
	}

	public void renderGUI() {
		for(int i = start; i < end; i++) {
			lines.get(i).draw(x, (i-start)*font.getHeight()+y);
		}
		Gui.color(cursorRed, 0.45f, 0.45f, 1*visibility);
		Gui.renderQuad(cursorX, cursorY, 6, cursorH);
		Gui.color(0.2f, 0.2f, 0.2f, 0.2f*visibility);
		Gui.renderQuad(cursorX, y, 6, h);
		Gui.color(0.1f, 0.1f, 0.1f, 0.35f*visibility);
		Gui.renderQuad(x, y, w, h);
	}

	public void anchorUpdate() {
		int yp = cursorY - y, yq = maxY - y;
		anchorUpdateMain();
		cursorY = y + yp;
		maxY = y + yq;
		cursorX = x + w - 6;
	}

	//TODO: Corriger BUG d'espaces en début de ligne
	public void addLine(Color[] colors, String[] text) {
		String currentLine = "";
		int i0 = 0, i = 0;
		while(i < text.length) {
			if(font.getWidth(currentLine + text[i]) <= w-6) {
				currentLine += text[i];
				i ++;
			} else {
				int j = 0;
				String[] s = text[i].split(" ");
				String t = "";
				while(font.getWidth(currentLine + t + s[j]) <= w-6) {
					t += s[j] + " ";
					j ++;
				}
				if(j == 0) {
					Color[] c = new Color[i-i0];
					String[] st = new String[i-i0];
					for(int k = i0; k < i; k++) {
						c[k-i0] = colors[k];
						st[k-i0] = text[k];
					}
					lines.add(new Line(c, st));
				} else {
					Color[] c = new Color[i-i0+1];
					String[] st = new String[i-i0+1];
					for(int k = i0; k < i; k++) {
						c[k-i0] = colors[k];
						st[k-i0] = text[k];
					}
					c[i-i0] = colors[i];
					st[i-i0] = t;
					text[i] = text[i].substring(t.length());
					lines.add(new Line(c, st));
				}
				i0 = i;
				currentLine = "";
			}
		}
		Color[] c = new Color[i-i0];
		String[] st = new String[i-i0];
		for(int k = i0; k < i; k++) {
			c[k-i0] = colors[k];
			st[k-i0] = text[k];
		}
		lines.add(new Line(c, st));
		cursorH = Math.max(6, Math.min((h*numLin) / lines.size(), h));
		cursorY = y+h-cursorH;
		end = lines.size();
		start = Math.max(0, end-numLin);
		maxY = y+h-cursorH;
	}

	private class Line {
	
		private Color[] colors;
		private String[] text;
		private int[] offset;
		
		public Line(Color[] colors, String[] text) {
			this.colors = colors;
			this.text = text;
			offset = new int[text.length];
			offset[0] = 0;
			for(int i = 0; i < offset.length - 1; i++) {
				offset[i+1] = offset[i] + font.getWidth(text[i]);
			}
		}
	
		public void draw(int x, int y) {
			for(int i = 0; i < text.length; i++) {
				Gui.color(colors[i]);
				font.drawString(x + offset[i], y, text[i]);
			}
		}
	
	}

	public void setVisibility(float v) { visibility = v; }
	
}