package com.test;
import com.melanistics.TickHandler;
import com.melanistics.Init;
import com.melanistics.event.PlayerJumpEvent;
import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.entity.TileEntity;
import com.naronco.cubeshaft.level.tile.Tile;

@Init.Plugin(pluginID = "jump")
public class SimpleNamedClass 
{
	public static final Tile SimpleNamedTile = new SinpleNamedBlock(30, 7);

	@Init.Start
	public void SimpleNamedMethode() 
	{
		System.err.println("I live !!!");
		//Cubeshaft.game.player.inventory.add(SimpleNamedTile.id);
		//Cubeshaft.ticker.addRunnable(new UselessClass4());
		TickHandler.EVENT_BUS.registerEventListener(this);
		System.err.println("I die ???");
	}
	@Init.Event
	public void onJump(PlayerJumpEvent e)
	{
		e.jump *= 1.5;
	}
}
