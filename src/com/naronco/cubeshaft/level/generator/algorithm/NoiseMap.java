/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator.algorithm;

import java.util.*;

public class NoiseMap {
	public double[] values;
	public int w, h;

	public NoiseMap(int w, int h, int featureSize) {
		this.w = w;
		this.h = h;

		Random random = new Random();
		values = new double[w * h];

		for (int y = 0; y < w; y += featureSize)
			for (int x = 0; x < w; x += featureSize)
				setSample(x, y, random.nextFloat() * 2 - 1);

		int stepSize = featureSize;
		double scale = 1.0 / w;
		double scaleMod = 1;
		do {
			int halfStep = stepSize / 2;
			for (int y = 0; y < w; y += stepSize)
				for (int x = 0; x < w; x += stepSize) {
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + stepSize, y + stepSize);

					double e = (a + b + c + d) / 4.0
							+ (random.nextFloat() * 2 - 1) * stepSize * scale;
					setSample(x + halfStep, y + halfStep, e);
				}
			for (int y = 0; y < w; y += stepSize)
				for (int x = 0; x < w; x += stepSize) {
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + halfStep, y + halfStep);
					double e = sample(x + halfStep, y - halfStep);
					double f = sample(x - halfStep, y + halfStep);

					double H = (a + b + d + e) / 4.0
							+ (random.nextFloat() * 2 - 1) * stepSize * scale
							* 0.5;
					double g = (a + c + d + f) / 4.0
							+ (random.nextFloat() * 2 - 1) * stepSize * scale
							* 0.5;
					setSample(x + halfStep, y, H);
					setSample(x, y + halfStep, g);
				}
			stepSize /= 2;
			scale *= (scaleMod + 0.8);
			scaleMod *= 0.3;
		} while (stepSize > 1);
	}

	private double sample(int x, int y) {
		return values[(x & (w - 1)) + (y & (h - 1)) * w];
	}

	private void setSample(int x, int y, double value) {
		values[(x & (w - 1)) + (y & (h - 1)) * w] = value;
	}
}