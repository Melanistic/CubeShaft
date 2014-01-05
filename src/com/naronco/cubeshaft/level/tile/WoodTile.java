/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.tile;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.Mob;
import com.naronco.cubeshaft.mob.MobSkeleton;
import com.naronco.cubeshaft.particle.ParticleEngine;

public class WoodTile extends Tile {
	public WoodTile(int id) {
		super(id);
		this.texIndex = 18;
	}

	@Override
	public int getTexture(int side) {
		if (side == 0 || side == 1)
			return 19;
		return 18;
	}

	@Override
	public void destroy(Level level, int x, int y, int z,
			ParticleEngine particleEngine) {
		super.destroy(level, x, y, z, particleEngine);
		MobSkeleton mob = new MobSkeleton(level);

		mob.setPos(x + 0.5f, y + 1, z + 0.5f);
		level.entities.add(mob);
	}
}
