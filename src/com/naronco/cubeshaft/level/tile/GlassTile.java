/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import com.naronco.cubeshaft.level.*;

public class GlassTile extends Tile {
	protected GlassTile(int id) {
		super(id);
		this.texIndex = 5;
	}

	@Override
	public boolean canRender(Level level, int x, int y, int z, int renderType,
			int side) {
		if (level.getTile(x, y, z) == this.id)
			return false;
		return super.canRender(level, x, y, z, renderType, side);
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}
}
