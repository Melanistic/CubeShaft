package com.naronco.cubeshaft.mob.ai;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.IEntitySelector;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.Mob;

public class AIEntitySearch extends AIBase {
	private int rad;
	private List<Class<? extends Entity>> entitys;
	private Entity target = null;
	private double path = -1;

	public AIEntitySearch(int radius, Class<? extends Entity>... classes) {
		rad = radius;
		entitys = Arrays.asList(classes);
		target = Cubeshaft.game.player;
	}

	@Override
	public void task(Mob mob) {
		
		if (target == null || target.removed) {
			findNewTarget(mob);
			System.out.println(target);
		}
		if (target != null && !target.removed) {
			EntityWatchEntity(mob, target);
			mob.walk(mob.normalSpeed);

			// if(mob.xd<0.0001&&mob.zd<0.0001)
			{
				int rot = (int) Math.round((180.0F + mob.yRot) / 90D);
				int x = (int) mob.x, y = (int) mob.y, z = (int) mob.z;
				switch (rot) {
				case (0):
					x++;
					break;
				case (1):
					z++;
					break;
				case (2):
					z--;
					break;
				case (3):
					x--;
					break;
				}
				//mob.level.setTile(x, y, z, 1);
				if (mob.level.getTile(x, y, z) != 0)
					mob.jump();
			}
		}
	}

	private void findNewTarget(final Mob mob) {
		try {
			Level l = mob.level;
			List<Entity> e = l.getEntitysExcludingEntity(
					mob.aabb.copie().grow(rad, rad, rad), mob,
					new IEntitySelector() {
						@Override
						public boolean isValidEntity(Entity e) {
							if (entitys.contains(e.getClass())) {
								double d = getDistancetoEntity(e, mob);
								if (path < 0 || path > d) {
									path = d;
									target = e;
								}
								return true;
							}
							return false;
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double getDistancetoEntity(Entity e1, Entity e2) {
		float f1 = e1.x - e2.x;
		float f2 = e1.z - e2.z;

		return Math.sqrt(f1 * f1 + f2 * f2);
	}

	public static void EntityWatchEntity(Entity e, Entity toWatch) {
		double f1 = e.x - toWatch.x;
		double f2 = e.z - toWatch.z;
		float f = 0.5F;

		double d = Math.toDegrees(Math.atan2(f1, f2));
		if (Math.abs(d - e.yRot) > f) {
			if ((d - e.yRot) > f)
				e.yRot += f;
			if ((d - e.yRot) < f)
				e.yRot -= f;
		} else
			e.yRot = (float) d;
		// double dis = Math.sqrt(f1*f1+f2*f2);
		// double hig = e.y - toWatch.y;

		// e.xRot = (float) Math.toDegrees(Math.atan2(hig, dis));
	}
}
