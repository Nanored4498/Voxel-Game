package fr.coudert.game.world;

import java.util.Random;

public class Noise {

	private long seed;
	private Random rand;
	private int octave;
	private float amplitude;

	public Noise(long seed, int octave, float amplitue) {
		this.seed = seed;
		this.octave = octave;
		this.amplitude = amplitue;
		rand = new Random();
	}

	public float getNoise(float x, float z) {
		int xMin = (int) (x < 0 ? (x+1) / octave - 1 : x / octave);
		int xMax = xMin + 1;
		int zMin = (int) (z < 0 ? (z+1) / octave - 1 : z / octave);
		int zMax = zMin + 1;
		float t = (x - xMin * octave) / octave;
		return interpolate(interpolate(noise(xMin, zMin), noise(xMax, zMin), t), interpolate(noise(xMin, zMax), noise(xMax, zMax), t), (z - zMin * octave) / octave) * amplitude;
	}

	private float interpolate(float a, float b, float t) {
		float f = (float) (1 - Math.cos(t * (float)Math.PI)) * 0.5f;
		return a * (1 - f) + b * f; 
	}

	private float noise(float x, float z) {
		rand.setSeed((long) (10000 * (Math.sin(x + Math.cos(z)) + Math.tan(seed))));
		return rand.nextFloat();
	}

}