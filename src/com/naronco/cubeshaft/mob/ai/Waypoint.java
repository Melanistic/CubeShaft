package com.naronco.cubeshaft.mob.ai;


import java.util.ArrayList;
import java.util.List;

import com.naronco.cubeshaft.Entity;

public class Waypoint implements Cloneable
{
	private Entity target;
	private int x, y, z;
	private boolean dynamik;
	
	public Waypoint parent = null;
	public State state = null;
	
	public Waypoint(Entity par1)
	{
		target=par1;
		dynamik = true;
	}
	
	public Waypoint(int par1, int par2, int par3)
	{
		x = par1;
		y = par2;
		z = par3;
		dynamik = false;
	}
	
	
	public boolean isDynamik()
	{
		return dynamik;
	}
	
	public Entity getEntity()
	{
		return target;
	}
	
	public float x()
	{
		return dynamik ? target.x : x;
	}
	public float y()
	{
		return dynamik ? target.y : y;
	}
	public float z()
	{
		return dynamik ? target.z : z;
	}
	
	public double getDistansTo(Waypoint w)
	{
		float x, y, z;
		
		x = x() - w.x();
		y = y() - w.y();
		z = z() - w.z();
		
		return Math.sqrt(x*x + y*y + z*z);
	}

	@Override
	public boolean equals(Object arg0) 
	{
		if(arg0 instanceof Waypoint)
		{
			Waypoint w = (Waypoint) arg0;
			return x==(int)w.x() && y==(int)w.y && z==(int)w.z;
		}
		return super.equals(arg0);
	}
	
	@Override
	protected Waypoint clone()
	{
		Waypoint w = dynamik? new Waypoint(target) : new Waypoint(x, y, z);
		w.parent = parent;
		return w;
	}
	
	@Override
	public String toString() 
	{
		return "Waypoint" + (dynamik ? "Entity; "+ target : (" x="+x()+" y="+y()+" z="+z())) + " "+state;
	}
	
	public List<Waypoint> getParents()
	{
		List<Waypoint> p = new ArrayList<Waypoint>();
		Waypoint parent = this.parent;
		while(parent!=null)
		{
			p.add(parent);
			parent = parent.parent;
		}
		return p;
		
	}
	
	public enum State
	{
		open, closed, part
	}
}
