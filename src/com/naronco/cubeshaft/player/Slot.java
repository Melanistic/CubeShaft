/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.player;

import com.naronco.cubeshaft.level.tile.Tile;

public class Slot {
	public ItemInstance item;
	public boolean grabbing = false;
	public int grabTime = 0;

	public Slot() {
	}

	public Slot(ItemInstance item) {
		this.item = item;
	}

	public boolean isFree() {
		return item == null;
	}

	public boolean contains(int id) {
		return item != null && item.tile.id == id;
	}

	public void add(int tile, int count) {
		if (isFree()) {
			item = new ItemInstance(Tile.tiles[tile], count);
		} else {
			item.count += count;
		}
		grabTime = 5;
		grabbing = true;
	}

	public void remove(int count) {
		item.count -= count;
		if (item.count <= 0) {
			item = null;
		}
	}

	public void tick() {
		if (grabTime > 0)
			grabTime--;
		else
			grabbing = false;
	}
}
