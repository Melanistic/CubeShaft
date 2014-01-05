package com.naronco.cubeshaft.level.tile;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.phys.AABB;

public class BambooTile extends Tile {
	public BambooTile(int id, int texIndex) {
		super(id, texIndex);
		float f = 1.0f / 32.0f;
		super.setBounds(f * 12.0f, 0.0f, f * 12.0f, 1 - f * 12.0f, 1.0f,
				1 - f * 12.0f);
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}

	@Override
	public AABB getAABB(int x, int y, int z) {
		return new AABB(x + x0, y + y0, z + z0, x + x1, y + y1, z + z1);
	}

	@Override
	public boolean canRender(Level level, int x, int y, int z, int renderType,
			int side) {
		if (renderType == 2)
			return false;
		if (side == 0 || side == 1) {
			Tile tile = Tile.tiles[level.getTile(x, y, z)];
			if (tile != null && tile.isNormalRender())
				return false;
		}
		return true;
	}
}
