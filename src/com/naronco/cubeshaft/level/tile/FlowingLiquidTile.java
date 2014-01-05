/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import java.util.Random;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.phys.AABB;

public class FlowingLiquidTile extends Tile {
	protected int liquidType;
	protected int flowTile;
	protected int liquidTile;

	protected FlowingLiquidTile(int id, int liquidType) {
		super(id);
		this.liquidType = liquidType;
		this.texIndex = 14;
		if (liquidType == 2)
			this.texIndex = 30;
		this.flowTile = id;
		this.liquidTile = id + 1;
		float h = 4.0f / 16.0f;
		setBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f - h, 1.0f);
	}

	@Override
	public void tick(Level level, int x, int y, int z, Random random) {
		System.out.println("Flowing");
		boolean flowing = false;
		int tileId = level.getTile(x, y - 1, z);
		if (tileId == 0) {
			level.setTile(x, y - 1, z, flowTile);
			flowing = true;
		} else {
			if (tileId == flowTile || tileId == liquidTile) {
				level.setTile(x, y - 1, z, flowTile);
				flowing = true;
			}
		}
		if (!flowing) {
			if (level.getTile(x - 1, y, z) == 0)
				level.setTile(x - 1, y, z, flowTile);
			if (level.getTile(x + 1, y, z) == 0)
				level.setTile(x + 1, y, z, flowTile);
			if (level.getTile(x, y, z - 1) == 0)
				level.setTile(x, y, z - 1, flowTile);
			if (level.getTile(x, y, z + 1) == 0)
				level.setTile(x, y, z + 1, flowTile);
		}
	}

	@Override
	public boolean canRender(Level level, int x, int y, int z, int i, int side) {
		int tile = level.getTile(x, y, z);
		if (side == 1 && tile > 0
				&& Tile.tiles[tile].getLiquidType() != liquidType)
			return true;
		if (i != 2 && liquidType == 1)
			return false;
		if (tile == this.flowTile || tile == this.liquidTile)
			return false;
		return super.canRender(level, x, y, z, -1, side);
	}

	@Override
	public int getRenderMode() {
		return 2;
	}

	@Override
	public AABB getAABB(int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public int getLiquidType() {
		return liquidType;
	}

	@Override
	public void neighborChange(Level level, int x, int y, int z, int id) {
		if (liquidType == 1
				&& (id == Tile.flowingLava.id || id == Tile.lava.id))
			level.setTile(x, y, z, Tile.stone.id);
		if (liquidType == 2
				&& (id == Tile.flowingWater.id || id == Tile.water.id))
			level.setTile(x, y, z, Tile.stone.id);
	}
}
