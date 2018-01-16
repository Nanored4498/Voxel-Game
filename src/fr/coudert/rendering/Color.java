package fr.coudert.rendering;

public class Color {

	public static final Color WHITE = new Color(1, 1, 1);
	public static final Color RED = new Color(1, 0, 0);
	public static final Color BLOOD = new Color(0.8f, 0.05f, 0.02f);
	public static final Color CHAT = new Color(0.05f, 0.05f, 0.18f);

	public float r, g, b, a;

	public Color(float r, float g, float b) {
		this(r, g, b, 1);
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(long hex) {
		a = ((hex & 0xFF000000) >> 24) / 255f;
		r = ((hex & 0xFF0000) >> 16) / 255f;
		g = ((hex & 0xFF00) >> 8) / 255f;
		b = (hex & 0xFF) / 255f;
	}

	public Color(Color other) {
		r = other.r;
		g = other.g;
		b = other.b;
		a = other.a;
	}

	public void setRGB(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color copy() {
		return new Color(r, g, b, a);
	}

	public Color mul(float m) {
		r *= m;
		g *= m;
		b *= m;
		return this;
	}

	public Color interpolate(Color c, float t) {
		return new Color(interpolate(r, c.r, t), interpolate(g, c.g, t), interpolate(b, c.b, t));
	}

	private static float interpolate(float a, float b, float t) {
		if(Math.min(a, b) == a)
			return a + (b - a) * t;
		else
			return a - (a - b) * t;
	}

	public long getHex() {
		return (long) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
	}

	public boolean equals(Color other) {
		return a == other.a && r == other.r && g == other.g && b == other.b;
	}

	public String toString() {
		return "r : " + r + " g : " + g + " b : " + b + " a : " + a;
	}

}