/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.Random;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.phys.AABB;

public class SaplingTile extends TallGrassTile {
	protected SaplingTile(int id) {
		super(id);
		this.texIndex = 15;
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		int lowerTile = level.getTile(x, y - 1, z);
		if (lowerTile != Tile.dirt.id && lowerTile != Tile.grass.id) {
			level.setTile(x, y, z, 0);
		}
		int randomCount = 128;
		if (random.nextInt(randomCount) == 0 && level.maybeGrowTree(x, y, z)) {
			level.growTree(x, y, z);
		}
	}

	@Override
	public int getRenderMode() {
		return 1;
	}

	@Override
	public AABB getAABB(int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}
}
