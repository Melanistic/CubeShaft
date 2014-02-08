package com.melanistics.event;

public class Event 
{
	private boolean cancled;
	
	public boolean isCancelable()
	{
		return true;
	}
	
	public boolean isCancled()
	{
		return isCancelable() ? cancled : false;
	}
	
	public void setCancled(boolean par1)
	{
		cancled = par1;
	}
	
	public boolean hasResult()
	{
		return false;
	}
	
	public Object getResult()
	{
		return null;
	}
}
