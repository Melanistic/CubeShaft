package com.naronco.cubeshaft;

import org.lwjgl.opengl.GL11;

import com.naronco.cubeshaft.gui.Menu;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.player.Player;

public class MenuInventory extends Menu 
{
	private Player pl;
	
	public MenuInventory(Player p)
	{
		pl = p;
	}
	
	@Override
	public void render(int xMouse, int yMouse) 
	{
		fill(0, 0, width, height, 0x99000000);
		super.render(xMouse, yMouse);
		GL11.glPushMatrix();
		for(int i=1;i < Tile.tiles.length; i++)
		{
			Tile t = Tile.tiles[i];
			if(t!=null)
			{
				//GL11.glTranslatef(2, 0, 0);
				//t.renderGui(0, 20, 40, 40);
			}
			else
			{
				break;
			}
		}
		GL11.glPopMatrix();
	}
}
