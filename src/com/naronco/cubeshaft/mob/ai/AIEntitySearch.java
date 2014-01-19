/**
 * @author MCenderdragon
 */
package com.naronco.cubeshaft.mob.ai;

import java.util.Arrays;
import java.util.List;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.IEntitySelector;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.mob.Mob;

public class AIEntitySearch extends AIBase {
	private int rad;
	private List<Class<? extends Entity>> entitys;
	//private Entity target = null;
	private double path = -1;
	private long lasttime = 0;
	private Thread search;

	public AIEntitySearch(int radius, Class<? extends Entity>... par2) {
		rad = radius;
		entitys = Arrays.asList(par2);
		//target = Cubeshaft.game.player;
	}

	@Override
	public void task(final Mob mob) {
		
		if (mob.target == null || mob.target.removed) {
			findNewTarget(mob);
		}
		if (mob.target != null && !mob.target.removed) 
		{
			if(mob.navigator.havePath() || search!=null && (search.isAlive() || search.isInterrupted()))
			{
				return;
			}
			else if(System.currentTimeMillis() - lasttime > 1000)
			{
				lasttime = System.currentTimeMillis();
				search = new Thread(new Runnable() 
				{			
					@Override
					public void run() 
					{
						try 
						{
							Thread t2 = new Thread(new Runnable() 
							{
								@Override
								public void run() 
								{
									Waypoint w = new Waypoint((int) mob.target.x, (int) mob.target.y+1, (int) mob.target.z);
									Path p = Path.getPath(mob.level, new Waypoint(mob), w, mob.getNormalSpeed());
									mob.navigator.setPath(p);
								}
							}, "Path Thread "+mob);
							t2.start();
							t2.join(1000);
							t2.interrupt();
						
							Thread.yield();
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
				});
				search.start();
			}
			
			
			
			/*
			lasttime--;
			if(lasttime<=0)
			{
				EntityWatchEntity(mob, mob.target);
				lasttime=2;
			}
			
			mob.walk(mob.normalSpeed);

			// if(mob.xd<0.0001&&mob.zd<0.0001)
			{
				int rot = (int) Math.round((180.0F + mob.yRot) / 90D);
				int x1 = (int) mob.x, y1 = (int) mob.y, z1 = (int) mob.z;
				int x2 = (int) mob.x, y2 = (int) mob.y, z2 = (int) mob.z;
				
				switch (rot) {
				case (0):x1++;
						break;
				case (1):z1++;
						break;
				case (2):z1--;
						break;
				case (3):x1--;
						break;
				}
				if((rot*90)>mob.yRot)rot++;
				if((rot*90)<mob.yRot)rot--;
				
				switch (rot) {
				case (0):x2++;
						break;
				case (1):z2++;
						break;
				case (2):z2--;
						break;
				case (3):x2--;
						break;
				}
				
				if (mob.level.getTile(x1, y1, z1) != 0 || mob.level.getTile(x2, y2, z2) != 0)
					mob.jump();
			}*/
		}
	}

	private void findNewTarget(final Mob mob) 
	{
		try 
		{
			Level l = mob.level;
			l.getEntitysExcludingEntity(mob.aabb.copie().grow(rad, rad, rad), mob, new IEntitySelector() 
			{
				@Override
				public boolean isValidEntity(Entity e) 
				{
					if (entitys.contains(e.getClass())) 
					{
						double d = getDistancetoEntity(e, mob);
						
						if (path < 0 || path > d) {
							path = d;
							mob.target = e;
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

	public static void EntityWatchEntity(Entity e, Entity toWatch) 
	{
		double f1 = e.x - toWatch.x;
		double f2 = e.z - toWatch.z;

		double d = Math.toDegrees(Math.atan2(f1, f2));
		e.yRot = (float) d;
		
		double dis = Math.sqrt(f1*f1+f2*f2);
		double hig = toWatch.y - e.y;
		
		e.xRot = (float) Math.toDegrees(Math.atan2(dis, hig));
		
	}
}
