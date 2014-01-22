package com.naronco.cubeshaft.phys;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class HitBox 
{
	private List<AABB> parts = new ArrayList<AABB>();
	private List<String> names = new ArrayList<String>();
	
	public void addALL(List<AABB> par1, List<String> par2)
	{
		if(par1.size() != par2.size())
		{
				throw new InvalidParameterException("Lists havent same lenght");
		}
		parts.addAll(par1);
		names.addAll(par2);
	}
	
	/**
	 * returns all Boxes at the give Location
	 */
	public List<AABB> getRealPosition(float x, float y, float z)
	{	
		synchronized (parts) 
		{
			List<AABB> real = new ArrayList<AABB>();
			
			for(AABB aabb : parts)
			{
				AABB bb = new AABB(aabb.x0 + x, aabb.y0 + y, aabb.z0 + z, aabb.x1+ x, aabb.y1 + y, aabb.z1 + z);
				real.add(bb);
			}
			return real;
		}
		
	}
	
	/**
	 * Add a Box with Name
	 */
	public void addNamedBox(AABB aabb, String name)
	{	
		parts.add(aabb);
		names.add(name);
	}
	
	/**
	 * Get the Name from a Box
	 */
	public String getName(AABB aabb)
	{
		synchronized (parts) 
		{
			for(int i=0;i<parts.size();i++)
			{
				if(parts.get(i)==aabb)
				{
					return names.get(i);
				}
			}
			return null;
		}
	}
	
	/**
	 * Get the a Box by its NAme
	 */
	public AABB getBox(String name)
	{
		synchronized (names) 
		{
			for(int i=0;i<names.size();i++)
			{
				if(names.get(i).equals(name))
				{
					return parts.get(i);
				}
			}
			return null;
		}
	}
	
	/**
	 * Gives the total Box at tthe location (from minimum to maximum)
	 */
	public AABB getTotalBox(float x, float y, float z)
	{
		float x0=0, y0=0, z0=0, x1=0, y1=0, z1=0;
		synchronized (parts) 
		{
			for(AABB bb : parts)
			{
				x0 = Math.min(bb.x0, x0);
				y0 = Math.min(bb.y0, y0);
				z0 = Math.min(bb.z0, z0);
				
				x1 = Math.max(bb.x1, x1);
				y1 = Math.max(bb.y1, y1);
				z1 = Math.max(bb.z1, z1);
			}
		}
		return new AABB(x0 + x, y0 + y, z0 + z, x1 + x, y1 + y, z1 + z);
	}
	
	
	public static HitBox getHumanModel(float height, float width)
	{
		HitBox box = new HitBox();
		float w = width / 2.0F;
		box.addNamedBox(new AABB(-w/2.0F, height/3.0F, -w/2.0F, w/2.0F, height, w/2.0F), "head");
		box.addNamedBox(new AABB(-w, 0, -w, w, height *2.0F /3.0F, w), "body");
		return box;
	}
	
}
