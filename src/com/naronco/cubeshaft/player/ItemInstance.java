/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.player;

import com.naronco.cubeshaft.level.tile.Tile;

public class ItemInstance {
	public Tile tile;
	public int count;

	public ItemInstance(Tile tile, int count) {
		this.tile = tile;
		this.count = count;
	}
}
