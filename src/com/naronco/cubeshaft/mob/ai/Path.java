/**
 * @author MCenderdragon
 */
package com.naronco.cubeshaft.mob.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.mob.ai.Waypoint.State;

public class Path 
{
	private static int maxrange = 32;
	
	private List<Waypoint> points;
	private int currentpoint;
	private Level l;
	private float speed;
	
	private Path(Level par1, float speed)
	{
		points = new ArrayList<Waypoint>();
		currentpoint = 0;
		l = par1;
		this.speed = speed;
	}
	
	public Waypoint getCurrendPoint()
	{
		return points.get(currentpoint);
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public boolean nextWaypoint()
	{
		if(currentpoint+1<points.size())
		{
			currentpoint++;
			return true;
		}
		return false;
	}
	
	public boolean checkPath(Waypoint p1)
	{
		Waypoint p2 = points.get(currentpoint);
		
		Waypoint p3 = points.get(points.size()-1);
		
		double d1 = p1.getDistansTo(p3);
		double d2 = p2.getDistansTo(p3);
		
		if(d1<d2)
		{
			Path p = getPath(l, p1, p3, speed);
			if(p != null)
			{
				points = p.points;
				currentpoint = 0;
				
			}
			return true;
		}
		return false;
	}
	
	public boolean end()
	{
		return currentpoint + 1 >= points.size();
	}
	
	public void addWaypoint(Waypoint w)
	{
		points.add(w);
	}
	
	public List<Waypoint> getPoints()
	{
		return points;
	}
	
	public void clear()
	{
		points.clear();		
	}
	
	@Override
	public String toString() 
	{
		return "Path: " +Arrays.toString(points.toArray());
	}
	
	public static Path getPath(Level l, Waypoint start, Waypoint end, float speed)
	{
		System.out.println("search path for "+end);
		Path p = new Path(l, speed);
		List<Waypoint> all = new ArrayList<Waypoint>();
		//List<Waypoint> all = Main.main.all;
		Waypoint current = start;
		
		while(!current.equals(end))
		{
			Waypoint next = current;
			try {
				next = next(l, current, end, all);
			}catch (Exception e) {
				current.state = State.closed;
			}
			if(next.equals(end))
			{
				current = next;
				break;
			}
			if(next!=current)
			{
				current = next;
			}
			else
			{
				int  opens = 0;
				//synchronized (all) 
				{
					List<Waypoint> buffer = new ArrayList<Waypoint>(all);
					for(Waypoint w : buffer)
					{
						if(w.equals(end))
						{
							current = w;
							break;
						}
						if(w.state==State.open)
						{
							if(start.getDistansTo(w)>maxrange)
							{
								w.state = State.closed;
								continue;
							}
							opens++;
							try {
							
								Waypoint w2 = next(l, w, end, all);
								
								if(w2.equals(end))
								{
									current = w2;
									break;
								}	
								if(w2.getDistansTo(end)<current.getDistansTo(end))
								{
									current = w2;
								}
							} catch(Exception e) {
								w.state = State.closed;
							}
						}
					}
					if(opens<=0)
						break;
				}		
			}
		}		
		
		p.points = current.getParents();
		p.points.add(current);
		System.out.println(p);
		return p;
	}
	
	private static Waypoint next(Level l, Waypoint main, Waypoint end, List<Waypoint> all)
	{
		Waypoint current = main;
		
		for(int x=0;x<3;x++)
		{
			for(int z=0;z<3;z++)
			{
				int x1, y1=0, z1;
				x1 = (int) ((main.x()-1) + x);
				z1 = (int) ((main.z()-1) + z);
				y1 = l.getHeigh(x1, z1);
				int id = l.getTile(x1, y1-1, z1);
				if(id!=0)
				{
					Tile t = Tile.tiles[id];
					if(t.getAABB(x1, y1-1, z1)==null)
					{
						y1--;
					}
				}
				Waypoint w = new Waypoint(x1, y1, z1);
				w.parent = main;
				main.state = State.part;
				
				boolean off = false;
				for(Waypoint know : all)
				{
					if(know.equals(end))
					{
						return know;
					}
					if(know.x()==w.x() && know.y()==w.y() && know.z()==w.z())
					{
						off=true;
						break;
					}
				}				
				if(!off)
				{		
					if(y1-current.y()>-5 && y1-current.y()<2)
					{
						w.state = State.open;
						
						if(w.getDistansTo(end)<current.getDistansTo(end))
						{
							current = w;
						}					
					}
					else
					{
						w.state = State.closed;
					}
					all.add(w);
					if(w.equals(end))		
							return w;
			
				}
			}
		}
		return current;
	}
}
