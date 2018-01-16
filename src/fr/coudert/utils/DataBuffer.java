package fr.coudert.utils;

import java.io.*;
import java.nio.ByteBuffer;

import fr.coudert.maths.Vec2;
import fr.coudert.maths.Vec3;

public class DataBuffer {

//	public static final int	HALF_BYTE	= 0xf;
//	public static final int	ONE_BYTE	= 0xff;
//	public static final int	TWO_BYTE	= 0xffff;
//	public static final int	THREE_BYTE	= 0xffffff;
//	public static final int	FOUR_BYTE	= 0xffffffff;

	private int currId;
	private byte[] data;

	public DataBuffer() {
		this(2048);
	}
	
	public DataBuffer(byte[] data) {
		this.data = data;
		this.currId = 0;
	}

	public DataBuffer(int size) {
		this.data = new byte[size];
		this.currId = 0;
	}

	public void flip() {
		byte[] temp = new byte[currId];
		for (int i = 0; i < currId; i++)
			temp[i] = data[i];
		data = temp;
	}

	public void clear() {
		data = new byte[data.length];
		currId = 0;
	}

	public void put(byte value) {
		if (currId >= data.length) {
			System.err.println("Write Overflow..." + currId + "\n\tMax capacity: " + data.length);
			return;
		}
		data[currId] = value;
		currId++;
	}

	public void put(byte... values) {
		for (int i = 0; i < values.length; i++)
			put(values[i]);
	}

	public byte getByte() {
		if (currId >= data.length) {
			System.err.println("Read Overflow..." + currId + "\n\tMax capacity: " + data.length);
			return 0;
		}
		return data[currId++];
	}

	public void put(short w) {
		put((byte) (w >> 8));
		put((byte) (w));
	}

	public short getShort() {
		return ByteBuffer.wrap(new byte[]
		{ getByte(), getByte() }).getShort();
	}

	public void put(int w) {
		put((byte) (w >> 24));
		put((byte) (w >> 16));
		put((byte) (w >> 8));
		put((byte) (w));
	}

	public int getInt() {
		return ByteBuffer.wrap(new byte[]
		{ getByte(), getByte(), getByte(), getByte() }).getInt();
	}

	public void put(long w) {
		put((byte) (w >> 56));
		put((byte) (w >> 48));
		put((byte) (w >> 40));
		put((byte) (w >> 32));
		put((byte) (w >> 24));
		put((byte) (w >> 16));
		put((byte) (w >> 8));
		put((byte) w);
	}

	public long getLong() {
		return ByteBuffer.wrap(new byte[]
		{ getByte(), getByte(), getByte(), getByte(), getByte(), getByte(), getByte(), getByte() }).getLong();
	}

	public void put(float w) {
		put(Float.floatToIntBits(w));
	}

	public float getFloat() {
		return ByteBuffer.wrap(new byte[]
		{ getByte(), getByte(), getByte(), getByte() }).getFloat();
	}

	public void put(double w) {
		put(Double.doubleToLongBits(w));
	}

	public double getDouble() {
		return ByteBuffer.wrap(new byte[]
		{ getByte(), getByte(), getByte(), getByte(), getByte(), getByte(), getByte(), getByte() }).getDouble();
	}

	public void put(String w) {
		byte[] b = w.getBytes();
		put(b.length);
		put(b);
	}

	public String getString() {
		byte[] b = new byte[getInt()];
		for (int i = 0; i < b.length; i++)
			b[i] = getByte();
		return new String(b);
	}

	public void put(Vec2 w) {
		put(w.x);
		put(w.y);
	}

	public Vec2 getVec2() {
		return new Vec2(getFloat(), getFloat());
	}

	public void put(Vec3 w) {
		put(w.x);
		put(w.y);
		put(w.z);
	}

	public Vec3 getVec3() {
		return new Vec3(getFloat(), getFloat(), getFloat());
	}

	public void write(String path) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(path);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean read(String path) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			fis.read(data);
			fis.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void setData(byte[] data) { this.data = data; }
	public byte[] getData() { return data; }
	public int getLength() { return data.length; }

}