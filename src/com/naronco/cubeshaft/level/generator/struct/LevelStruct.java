/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.level.generator.struct;

import java.util.*;

import com.naronco.cubeshaft.level.*;

public abstract class LevelStruct {
	public abstract void generate(Level level, int x, int y, int z,
			Random random);
}
