package com.melanistics.event;

import com.naronco.cubeshaft.player.Player;

public class PlayerJumpEvent extends Event 
{
	private Player pl;
	public float jump;
	
	public PlayerJumpEvent(Player p, float f)
	{
		pl = p;
		jump = f;
	}
	
	public Player getPlayer()
	{
		return pl;
	}
	
	@Override
	public boolean hasResult() 
	{
		return true;
	}
	
	@Override
	public Object getResult() 
	{
		return jump;
	}
}
