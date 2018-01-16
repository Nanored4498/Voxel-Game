package fr.coudert.maths;

public class Vec3 {

	public static final Vec3 RIGHT = new Vec3(1, 0, 0);
	public static final Vec3 UP = new Vec3(0, 1, 0);
	public static final Vec3 FORWARD = new Vec3(0, 0, 1);
	public static final Vec3 LEFT = new Vec3(-1, 0, 0);
	public static final Vec3 DOWN = new Vec3(0, -1, 0);
	public static final Vec3 BEHIND = new Vec3(0, 0, -1);
	public static final Vec3 ZERO = new Vec3(0, 0, 0);

	public float x;
	public float y;
	public float z;

	public Vec3() {
		this(0, 0, 0);
	}

	public Vec3(Vec3 vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vec3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public Vec3 copy() {
		return new Vec3(this);
	}

	public float length() {
		return (float)Math.sqrt(x*x + y*y + z*z);
	}

	public Vec3 normalized() {
		float length = length();
		x /= length;
		y /= length;
		z /= length;
		return this;
	}

	public Vec3 add(Vec3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vec3 sub(Vec3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vec3 mul(Vec3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}

	public Vec3 mul(float f) {
		x *= f;
		y *= f;
		z *= f;
		return this;
	}

	public Vec3 div(Vec3 v) {
		x /= v.x;
		y /= v.y;
		z /= v.z;
		return this;
	}

	public float max() {
		return Math.max(x, Math.max(y, z));
	}

	public float min() {
		return Math.min(x, Math.min(y, z));
	}

	public Vec3 check() {
		float max = max();
		float min = min();
		float v = 0;
		if(Math.abs(max - 1) > Math.abs(min))
			v = min;
		else
			v = max;
		int rv = v > 0.5f ? 1 : -1;
		return new Vec3(v == x ? rv : 0, v == y ? rv : 0, v == z ? rv : 0);
	}

	public Vec3 cross(Vec3 v) {
		set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
		return this;
	}

	public Vec3 lerp(Vec3 v, float f) {
		set(x + (v.x-x)*f, y + (v.y-y)*f, z + (v.z-z)*f);
		return this;
	}

	public float dot(Vec3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public boolean equals(Vec3 v) {
		return x == v.x && y == v.y && z == v.z;
	}

	public String toString() {
		return "x : " + x + " y : " + y + " z : " + z;
	}

}