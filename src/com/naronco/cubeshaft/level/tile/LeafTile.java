/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.*;

import com.naronco.cubeshaft.level.*;

public class LeafTile extends Tile {
	protected LeafTile(int id) {
		super(id);
		this.texIndex = 20;
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		int woodCount = 0;
		for (int xc = x - 4; xc <= x + 4; xc++) {
			for (int yc = y - 4; yc <= y + 4; yc++) {
				for (int zc = z - 4; zc <= z + 4; zc++) {
					int xd = Math.abs(xc - x);
					int yd = Math.abs(yc - y);
					int zd = Math.abs(zc - z);
					if (Math.sqrt(xd * xd + yd * yd + zd * zd) <= 4) {
						int tile = level.getTile(xc, yc, zc);
						if (tile == Tile.planks.id || tile == Tile.wood.id)
							woodCount++;
					}
				}
			}
		}
		if (woodCount == 0 && random.nextInt(128) == 0) {
			level.setTile(x, y, z, 0);
		}
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}
}
