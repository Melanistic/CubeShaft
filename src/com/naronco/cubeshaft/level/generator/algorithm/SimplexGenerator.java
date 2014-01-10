package com.naronco.cubeshaft.level.generator.algorithm;

import java.util.Random;

import com.naronco.cubeshaft.level.generator.Biome;

public class SimplexGenerator implements IGenerator {
	protected byte[] perm = new byte[512];
	float o, p, f;

	public SimplexGenerator(Random random, float octaves, float persistence, float frequency)
	{
		o = octaves;
		p = persistence;
		f = frequency;
		for (int i = 0; i < 512; i++)
		{
			perm[i] = (byte)random.nextInt(256);
		}
	}

	public float Generate(int x, int y, Biome biome)
	{
		return Noise(x, y) * (Biome.getMax(biome) - Biome.getMin(biome)) + Biome.getMin(biome);
	}

	public float Noise(int x, int y)
	{
		float octaves = o;
		float persistence = p;
		float frequency = f;
		float total = 0;
		float amplitude = 1;

		// We have to keep track of the largest possible amplitude,
		// because each octave adds more, and we need a value in [-1, 1].
		float maxAmplitude = 0;

		for( int i=0; i < octaves; i++ ) {
			total += RawGetHeight(x * frequency, y * frequency) * amplitude;

			frequency *= 2;
			maxAmplitude += amplitude;
			amplitude *= persistence;
		}

		return ((total / maxAmplitude) + 1) * 0.5f;
	}

	protected float RawGetHeight(float x, float y)
	{
		float F2 = 0.366025403f; // F2 = 0.5*(sqrt(3.0)-1.0)
		float G2 = 0.211324865f; // G2 = (3.0-Math.sqrt(3.0))/6.0

		float n0, n1, n2; // Noise contributions from the three corners

		// Skew the input space to determine which simplex cell we're in
		float s = (x + y) * F2; // Hairy factor for 2D
		float xs = x + s;
		float ys = y + s;
		int i = FastFloor(xs);
		int j = FastFloor(ys);

		float t = (float)(i + j) * G2;
		float X0 = i - t; // Unskew the cell origin back to (x,y) space
		float Y0 = j - t;
		float x0 = x - X0; // The x,y distances from the cell origin
		float y0 = y - Y0;

		// For the 2D case, the simplex shape is an equilateral triangle.
		// Determine which simplex we are in.
		int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
		if (x0 > y0) { i1 = 1; j1 = 0; } // lower triangle, XY order: (0,0)->(1,0)->(1,1)
		else { i1 = 0; j1 = 1; }      // upper triangle, YX order: (0,0)->(0,1)->(1,1)

		// A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
		// a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
		// c = (3-sqrt(3))/6

		float x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
		float y1 = y0 - j1 + G2;
		float x2 = x0 - 1.0f + 2.0f * G2; // Offsets for last corner in (x,y) unskewed coords
		float y2 = y0 - 1.0f + 2.0f * G2;

		// Wrap the integer indices at 256, to avoid indexing perm[] out of bounds
		int ii = i % 256;
		int jj = j % 256;

		// Calculate the contribution from the three corners
		float t0 = 0.5f - x0 * x0 - y0 * y0;
		if (t0 < 0.0f) n0 = 0.0f;
		else
		{
			t0 *= t0;
			if(ii + perm[jj] < 0) n0 = 0;
			else n0 = t0 * t0 * grad(perm[ii + perm[jj]], x0, y0);
		}

		float t1 = 0.5f - x1 * x1 - y1 * y1;
		if (t1 < 0.0f) n1 = 0.0f;
		else
		{
			t1 *= t1;
			if(ii + i1 + perm[jj + j1] < 0) n1 = 0;
			else n1 = t1 * t1 * grad(perm[ii + i1 + perm[jj + j1]], x1, y1);
		}

		float t2 = 0.5f - x2 * x2 - y2 * y2;
		if (t2 < 0.0f) n2 = 0.0f;
		else
		{
			t2 *= t2;
			if(ii + 1 + perm[jj + 1] < 0) n2 = 0;
			else n2 = t2 * t2 * grad(perm[ii + 1 + perm[jj + 1]], x2, y2);
		}

		// Add contributions from each corner to get the final noise value.
		// The result is scaled to return values in the interval [-1,1].
		return 40.0f * (n0 + n1 + n2); // TODO: The scale factor is preliminary!
	}

	protected int FastFloor(float x)
	{
		return (x > 0) ? ((int)x) : (((int)x) - 1);
	}

	protected float grad(int hash, float x, float y)
	{
		int h = hash & 7;      // Convert low 3 bits of hash code
		float u = h < 4 ? x : y;  // into 8 simple gradient directions,
		float v = h < 4 ? y : x;  // and compute the dot product with (x,y).
		return ((h & 1) != 0 ? -u : u) + ((h & 2) != 0 ? -2.0f * v : 2.0f * v);
	}
}
