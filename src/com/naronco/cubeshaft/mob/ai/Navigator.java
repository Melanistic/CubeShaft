/**
 * @author MCenderdragon
 */
package com.naronco.cubeshaft.mob.ai;

import com.naronco.cubeshaft.Entity;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.mob.Mob;

public class Navigator 
{
	private Path path;
	
	private Mob toNavigate;
	
	public Navigator(Mob e)
	{
		toNavigate = e;
	}
	
	public void setPath(Path p)
	{
		path = p;
		/*
		if(path!=null)
		{
			for(Waypoint w : path.getPoints())
			{
				toNavigate.level.setTile((int)w.x(), (int)w.y(), (int)w.z(), Tile.leaves.id);
			}
			toNavigate.removed = true;
		}
		*/
	}
	
	public void navigate()
	{
		if(path!=null)
		{
			Waypoint p = path.getCurrendPoint();
			Waypoint entity = new Waypoint(toNavigate);
			if(p.equals(entity) || p.getDistansTo(entity)<0.5F)
			{
				if(path.end())
				{
					path = null;
					return;
				}
				if(!path.checkPath(entity))
					path.nextWaypoint();
				p = path.getCurrendPoint();
			}
			rotateToWaypoint(toNavigate, p);
			
			toNavigate.walk(path.getSpeed());
			
			int rot = (int) Math.round((180.0F + toNavigate.yRot) / 90D);
			int x1 = (int) toNavigate.x, y1 = (int) toNavigate.y, z1 = (int) toNavigate.z;
			int x2 = (int) toNavigate.x, y2 = (int) toNavigate.y, z2 = (int) toNavigate.z;
			
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
			if((rot*90)>toNavigate.yRot)rot++;
			if((rot*90)<toNavigate.yRot)rot--;
			
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
			
			if (toNavigate.level.getTile(x1, y1, z1) != 0 || toNavigate.level.getTile(x2, y2, z2) != 0)
				toNavigate.jump();
			
			if(p.y()>entity.y() &&  p.getDistansTo(entity)<2.0F)
			{
				toNavigate.jump();
			}
			if(p.y()-entity.y() > 1)
			{
				path.checkPath(entity);
			}
		}
	}
	
	public static void rotateToWaypoint(Entity e, Waypoint w)
	{
		double f1 = e.x - w.x();
		double f2 = e.z - w.z();

		double d = Math.toDegrees(Math.atan2(f1, f2));
		e.yRot = (float) d;
		
		double dis = Math.sqrt(f1*f1+f2*f2);
		double hig = w.y() - e.y;
		
		e.xRot = (float) Math.toDegrees(Math.atan2(dis, hig));
	}
	
	public boolean havePath()
	{
		return path !=null;
	}
}
