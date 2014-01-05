/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.*;

import com.naronco.cubeshaft.level.*;

public class GrassTile extends Tile {
	protected GrassTile(int id) {
		super(id);
		this.texIndex = 3;
	}

	@Override
	public int getTexture(int side) {
		if (side == 1) {
			return 0;
		}
		if (side == 0) {
			return 2;
		}
		return 3;
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		if (random.nextInt(64) != 0) {
			return;
		}
		for (int i = 0; i < 4; i++) {
			int xx = x + random.nextInt(3) - 1;
			int yy = y + random.nextInt(5) - 3;
			int zz = z + random.nextInt(3) - 1;
			if (level.getTile(xx, yy, zz) == Tile.dirt.id) {
				level.setTile(xx, yy, zz, Tile.grass.id);
			}
		}
	}
}
