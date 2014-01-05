/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.Random;

import com.naronco.cubeshaft.entity.TileEntity;
import com.naronco.cubeshaft.level.Level;

public class GravelTile extends Tile {
	protected GravelTile(int id, int texIndex) {
		super(id, texIndex);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		int below = level.getTile(x, y - 1, z);
		if (below == 0) {
			level.setTile(x, y, z, 0);
			level.addEntity(new TileEntity(level, this, x, y, z));
		}
	}
}
