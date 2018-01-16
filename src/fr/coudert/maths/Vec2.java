package fr.coudert.maths;

public class Vec2 {

	public float x, y;

	public Vec2() {
		this(0, 0);
	}

	public Vec2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float length() {
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public Vec2 normalized() {
		float length = length();
		return new Vec2(x/length, y/length);
	}

	public Vec2 lerp(Vec2 destination, float factor) {
		set(x + (destination.x - x) * factor, y + (destination.y - y) * factor);
		return this;
	}

	public Vec2 rotate(float angle) {
		double cos = Math.cos(Math.toRadians(angle));
		double sin = Math.sin(Math.toRadians(angle));
		return new Vec2((float)(x*cos - y*sin), (float)(x*sin + y*cos));
	}

	public float dot(Vec2 v) {
		return x * v.x + y * v.y;
	}

	public float cross(Vec2 v) {
		return x * v.y - y * v.x;
	}

	public Vec2 add(Vec2 v) {
		return new Vec2(x+v.x, y+v.y);
	}

	public Vec2 sub(Vec2 v) {
		return new Vec2(x-v.x, y-v.y);
	}

	public Vec2 mul(Vec2 v) {
		return new Vec2(x*v.x, y*v.y);
	}

	public Vec2 mul(float f) {
		return new Vec2(x*f, y*f);
	}

	public Vec2 div(Vec2 v) {
		return new Vec2(x/v.x, y/v.y);
	}

	public boolean equals(Vec2 v) {
		return x == v.x && y == v.y;
	}

	public String toString() {
		return "x: " + x + "    y: " + y;
	}

}