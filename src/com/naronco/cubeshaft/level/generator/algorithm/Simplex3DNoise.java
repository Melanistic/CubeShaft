package com.naronco.cubeshaft.level.generator.algorithm;

import java.util.Random;

public class Simplex3DNoise {
	protected byte[] perm = new byte[512];
	float o, p, f;
	
	public Simplex3DNoise(Random random, float octaves, float persistence, float frequency)
	{
		o = octaves;
		p = persistence;
		f = frequency;
		for (int i = 0; i < 512; i++)
		{
			perm[i] = (byte)random.nextInt(256);
		}
	}
	
	public float Generate(int x, int y, int z)
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
			total += RawGenerate(x * frequency, y * frequency, z * frequency) * amplitude;

			frequency *= 2;
			maxAmplitude += amplitude;
			amplitude *= persistence;
		}

		return ((total / maxAmplitude) + 1) * 0.5f;
    }
	
	public float RawGenerate(float x, float y, float z)
	{
		float F3 = 0.333333333f;
        float G3 = 0.166666667f;

        float n0, n1, n2, n3; // Noise contributions from the four corners

        // Skew the input space to determine which simplex cell we're in
        float s = (x+y+z)*F3; // Very nice and simple skew factor for 3D
        float xs = x+s;
        float ys = y+s;
        float zs = z+s;
        int i = FastFloor(xs);
        int j = FastFloor(ys);
        int k = FastFloor(zs);

        float t = (float)(i+j+k)*G3; 
        float X0 = i-t; // Unskew the cell origin back to (x,y,z) space
        float Y0 = j-t;
        float Z0 = k-t;
        float x0 = x-X0; // The x,y,z distances from the cell origin
        float y0 = y-Y0;
        float z0 = z-Z0;

        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we are in.
        int i1, j1, k1; // Offsets for second corner of simplex in (i,j,k) coords
        int i2, j2, k2; // Offsets for third corner of simplex in (i,j,k) coords

        /* This code would benefit from a backport from the GLSL version! */
        if(x0>=y0) {
            if(y0>=z0)
            { i1=1; j1=0; k1=0; i2=1; j2=1; k2=0; } // X Y Z order
            else if(x0>=z0) { i1=1; j1=0; k1=0; i2=1; j2=0; k2=1; } // X Z Y order
            else { i1=0; j1=0; k1=1; i2=1; j2=0; k2=1; } // Z X Y order
            }
        else { // x0<y0
            if(y0<z0) { i1=0; j1=0; k1=1; i2=0; j2=1; k2=1; } // Z Y X order
            else if(x0<z0) { i1=0; j1=1; k1=0; i2=0; j2=1; k2=1; } // Y Z X order
            else { i1=0; j1=1; k1=0; i2=1; j2=1; k2=0; } // Y X Z order
        }

        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.

        float x1 = x0 - i1 + G3; // Offsets for second corner in (x,y,z) coords
        float y1 = y0 - j1 + G3;
        float z1 = z0 - k1 + G3;
        float x2 = x0 - i2 + 2.0f*G3; // Offsets for third corner in (x,y,z) coords
        float y2 = y0 - j2 + 2.0f*G3;
        float z2 = z0 - k2 + 2.0f*G3;
        float x3 = x0 - 1.0f + 3.0f*G3; // Offsets for last corner in (x,y,z) coords
        float y3 = y0 - 1.0f + 3.0f*G3;
        float z3 = z0 - 1.0f + 3.0f*G3;

        // Wrap the integer indices at 256, to avoid indexing perm[] out of bounds
        int ii = Mod(i, 256);
        int jj = Mod(j, 256);
        int kk = Mod(k, 256);

        // Calculate the contribution from the four corners
        float t0 = 0.6f - x0*x0 - y0*y0 - z0*z0;
        if(t0 < 0.0f) n0 = 0.0f;
        else {
            t0 *= t0;
            if(jj+perm[kk] < 0 || ii+perm[jj+perm[kk]] < 0) n0 = 0;
            else n0 = t0 * t0 * grad(perm[ii+perm[jj+perm[kk]]], x0, y0, z0);
        }

        float t1 = 0.6f - x1*x1 - y1*y1 - z1*z1;
        if(t1 < 0.0f) n1 = 0.0f;
        else {
            t1 *= t1;
            if(jj+j1+perm[kk+k1] < 0 || ii+i1+perm[jj+j1+perm[kk+k1]] < 0) n1 = 0;
            else n1 = t1 * t1 * grad(perm[ii+i1+perm[jj+j1+perm[kk+k1]]], x1, y1, z1);
        }

        float t2 = 0.6f - x2*x2 - y2*y2 - z2*z2;
        if(t2 < 0.0f) n2 = 0.0f;
        else {
            t2 *= t2;
            if(jj+j2+perm[kk+k2] < 0|| ii+i2+perm[jj+j2+perm[kk+k2]] < 0) n2 = 0;
            else n2 = t2 * t2 * grad(perm[ii+i2+perm[jj+j2+perm[kk+k2]]], x2, y2, z2);
        }

        float t3 = 0.6f - x3*x3 - y3*y3 - z3*z3;
        if(t3<0.0f) n3 = 0.0f;
        else {
            t3 *= t3;
            if(jj+1+perm[kk+1] < 0 || ii+1+perm[jj+1+perm[kk+1]] < 0) n3 = 0;
            else n3 = t3 * t3 * grad(perm[ii+1+perm[jj+1+perm[kk+1]]], x3, y3, z3);
        }

        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0f * (n0 + n1 + n2 + n3); // TODO: The scale factor is preliminary!
	}
	
	private int FastFloor(float x)
    {
        return (x > 0) ? ((int)x) : (((int)x) - 1);
    }

    private int Mod(int x, int m)
    {
        int a = x % m;
        return a < 0 ? a + m : a;
    }
    
    private float grad( int hash, float x, float y , float z ) {
        int h = hash & 15;     // Convert low 4 bits of hash code into 12 simple
        float u = h<8 ? x : y; // gradient directions, and compute dot product.
        float v = h<4 ? y : h==12||h==14 ? x : z; // Fix repeats at h = 12 to 15
        return ((h&1) != 0 ? -u : u) + ((h&2) != 0 ? -v : v);
    }
}
