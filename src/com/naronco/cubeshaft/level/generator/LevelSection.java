/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator;

import java.util.Random;

import com.naronco.cubeshaft.level.Level;

public abstract class LevelSection {
	public abstract String getLoadMessage();

	public abstract void generate(Level level, int width, int height,
			int depth, Random random);
}
