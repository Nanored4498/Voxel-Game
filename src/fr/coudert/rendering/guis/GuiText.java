package fr.coudert.rendering.guis;

import fr.coudert.rendering.Color;

public class GuiText extends GuiComponent {

	private TrueTypeFont font;
	private String text;
	private boolean centered;

	public GuiText(TrueTypeFont font, String text, int x, int y) {
		super(x, y, font.getWidth(text), font.getHeight());
		this.font = font;
		this.text = text;
		centered = false;
	}

	public void update() {}

	public void renderGUI() {
		Gui.color(Color.WHITE);
		font.drawString(x, y, text);
	}

	public void centerText() {
		x -= font.getWidth(text)/2;
		y -= font.getHeight()/2;
		centered = true;
	}

	public void setText(String text) {
		w = font.getWidth(text);
		if(centered)
			x += (font.getWidth(this.text) - w) / 2;
		this.text = text;
		anchorUpdate();
	}

	public String getText() { return text; }
	public TrueTypeFont getFont() { return font; }

}