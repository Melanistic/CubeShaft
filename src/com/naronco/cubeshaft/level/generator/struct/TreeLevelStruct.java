/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator.struct;

import java.util.*;

import com.naronco.cubeshaft.level.*;
import com.naronco.cubeshaft.level.tile.*;

public class TreeLevelStruct extends LevelStruct {
	public void generate(Level level, int x, int y, int z, Random random) {
		if (!level.maybeGrowTree(x, y, z))
			return;
		level.setTile(x, y - 1, z, Tile.dirt.id);
		int size = 6 + random.nextInt(5);
		for (int j = 0; j < 3; j++)
			for (int k = 0; k < 3; k++)
				for (int l = 0; l < 5; l++) {
					level.setTile(x - 1 + j, y + size - 2 + l, z - 1 + k,
							Tile.leaves.id);
					level.setTile(x - 2 + l, y + size - 1 + j, z - 1 + k,
							Tile.leaves.id);
					level.setTile(x - 1 + j, y + size - 1 + k, z - 2 + l,
							Tile.leaves.id);
				}
		for (int j = 0; j < size; j++) {
			level.setTile(x, y + j, z, Tile.wood.id);
		}
	}
}
