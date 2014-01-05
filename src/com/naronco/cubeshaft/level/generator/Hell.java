/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator;

import java.util.Random;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.generator.algorithm.NoiseMap;
import com.naronco.cubeshaft.level.tile.Tile;

public class Hell extends LevelSection {
	private Cubeshaft game;

	public Hell(Cubeshaft game) {
		this.game = game;
	}

	public String getLoadMessage() {
		return "Generating hell..";
	}

	public void generate(Level level, int width, int height, int depth,
			Random random) {
		NoiseMap noise = new NoiseMap(width, depth, 32);

		int genHeight = height / 8;

		for (int x = 0; x < width; x++) {
			game.setProgress(x * 100 / (width - 1));
			for (int z = 0; z < depth; z++) {
				float val = (float) noise.values[x + z * width];
				int hei = (int) (val * 20 + genHeight);
				for (int y = 0; y < hei; y++) {
					int id = 0;
					if (y == hei - 1)
						id = Tile.rock.id;
					level.setTileNoUpdate(x, y, z, id);
				}
			}
		}

		noise = new NoiseMap(width, depth, 32);

		for (int x = 0; x < width; x++) {
			game.setProgress(x * 33 / (width - 1));
			for (int z = 0; z < depth; z++) {
				float val = (float) noise.values[x + z * width];
				int hei = (int) (val * 40 + 16);
				for (int y = 0; y < hei; y++) {
					int id = Tile.rock.id;
					if (random.nextInt(5) == 0)
						id = Tile.hellStone.id;
					level.setTileNoUpdate(x, y, z, id);
				}
			}
		}

		for (int i = 0; i < 70; i++) {
			game.setProgress(33 + i * 33 / (100 - 1));
			int xx = random.nextInt(width);
			int yy = random.nextInt(genHeight);
			int zz = random.nextInt(depth);
			int r = random.nextInt(6) + 5;
			for (int x = xx - r; x <= xx + r; x++) {
				for (int y = yy - r; y <= yy + r; y++) {
					for (int z = zz - r; z <= zz + r; z++) {
						int xd = x - xx;
						int yd = y - yy;
						int zd = z - zz;
						if (xd * xd + yd * yd + zd * zd < r * r
								&& random.nextInt(5) / 4 != 0) {
							if (random.nextInt(80) == 0) {
								int ys = y;
								for (int yc = ys; yc >= 0; yc--) {
									int tile = level.getTile(x, yc, z);
									if (tile == 0) {
										level.setTileNoUpdate(x, yc, z,
												Tile.lava.id);
									} else {
										break;
									}
								}
							} else {
								level.setTileNoUpdate(x, y, z, Tile.rock.id);
							}
						}
					}
				}
			}
		}

		for (int x = 0; x < width; x++) {
			game.setProgress(66 + x * 34 / (width - 1));
			for (int y = 0; y < 18; y++) {
				for (int z = 0; z < depth; z++) {
					int tile = level.getTile(x, y, z);
					if (tile == 0) {
						level.setTileNoUpdate(x, y, z, Tile.lava.id);
					}
				}
			}
		}
	}
}
