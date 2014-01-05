/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.Random;

import com.naronco.cubeshaft.level.Level;

public class LiquidTile extends FlowingLiquidTile {
	protected LiquidTile(int id, int liquidType) {
		super(id, liquidType);
		this.flowTile = id - 1;
		this.liquidTile = id;
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		if ((level.getTile(x, y, z - 1) == 0)
				|| (level.getTile(x, y, z + 1) == 0)
				|| (level.getTile(x - 1, y, z) == 0)
				|| (level.getTile(x + 1, y, z) == 0)) {
			level.setTile(x, y, z, Tile.flowingWater.id);
		}

	}
}
