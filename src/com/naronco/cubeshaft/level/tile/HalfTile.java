/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

public class HalfTile extends Tile {
	protected HalfTile(int id, Tile tile) {
		super(id, tile.texIndex);
		setBounds(0, 0, 0, 1, 0.5f, 1);
	}

	@Override
	public boolean isNormalRender() {
		return false;
	}
}