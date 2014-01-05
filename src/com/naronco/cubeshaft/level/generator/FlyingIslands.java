/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator;

import java.util.Random;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.generator.algorithm.CombinedNoiseMap;
import com.naronco.cubeshaft.level.generator.algorithm.NoiseMap;
import com.naronco.cubeshaft.level.tile.Tile;

public class FlyingIslands extends LevelSection {
	private Cubeshaft game;

	public FlyingIslands(Cubeshaft game) {
		this.game = game;
	}

	public void generate(Level level, int width, int height, int depth,
			Random random) {
		game.setProgress(0);
		byte[] tmpTiles = new byte[width * height * depth];

		NoiseMap noise1 = new NoiseMap(width, depth, 32);
		NoiseMap noise2 = new NoiseMap(width, depth, 32);

		CombinedNoiseMap cnm = new CombinedNoiseMap(noise1, noise2);

		int renderHeight = height / 4 * 3;
		for (int x = 0; x < width; x++) {
			game.setProgress(x * 33 / (width - 1));
			for (int z = 0; z < depth; z++) {
				int h = (int) (cnm.values[x + z * width] * 4) + renderHeight;
				for (int y = 0; y < h; y++) {
					if (y >= 0 && y < height) {
						int index = (y * width + z) * depth + x;
						int id = 0;
						if (y == h - 1)
							id = Tile.grass.id;
						else if (y < h - 1
								&& y >= h - 2 * (random.nextInt(4) + 1))
							id = Tile.dirt.id;
						else if (y < h - 4)
							id = Tile.stone.id;
						tmpTiles[index] = (byte) id;
					}
				}
			}
		}

		NoiseMap noise11 = new NoiseMap(width, depth, 32);
		NoiseMap noise12 = new NoiseMap(width, depth, 32);

		CombinedNoiseMap cnm1 = new CombinedNoiseMap(noise11, noise12);

		for (int x = 0; x < width; x++) {
			game.setProgress(33 + x * 33 / (width - 1));
			for (int z = 0; z < depth; z++) {
				int h = (int) ((2.0 - cnm1.values[x + z * width]) * 14)
						+ renderHeight - 7;
				for (int y = 0; y < h; y++) {
					if (y >= 0 && y < height) {
						int index = (y * width + z) * depth + x;
						tmpTiles[index] = 0;
					}
				}
			}
		}

		for (int x = 0; x < width; x++) {
			game.setProgress(66 + x * 34 / (width - 1));
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					if (level.getTile(x, y, z) == 0) {
						level.setTileNoUpdate(x, y, z, tmpTiles[(y * width + z)
								* depth + x]);
					}
				}
			}
		}
	}

	public String getLoadMessage() {
		return "Floating..";
	}
}
