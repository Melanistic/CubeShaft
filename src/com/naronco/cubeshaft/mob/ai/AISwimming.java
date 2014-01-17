/**
 * @author MCenderdragon
 */
package com.naronco.cubeshaft.mob.ai;

import com.naronco.cubeshaft.mob.Mob;

public class AISwimming extends AIBase {
	@Override
	public void task(Mob mob) {
		if (mob.isHeadInWater()) {
			mob.yd = 0.05f;
		}
	}

}
