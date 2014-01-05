/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator.algorithm;

public class CombinedNoiseMap {
	public float[] values;

	public CombinedNoiseMap(NoiseMap noise1, NoiseMap noise2) {
		int w = noise1.w;
		int h = noise1.h;
		values = new float[w * h];
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++) {
				int i = x + y * w;
				float val = (float) (Math.abs(noise1.values[i]
						- noise2.values[i]) * 3 - 2);
				float xd = x / (w - 1.0f) * 2 - 1;
				float yd = y / (h - 1.0f) * 2 - 1;
				if (xd < 0)
					xd = -xd;
				if (yd < 0)
					yd = -yd;
				float dist = xd >= yd ? xd : yd;
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				values[i] = val;
			}
	}
}
