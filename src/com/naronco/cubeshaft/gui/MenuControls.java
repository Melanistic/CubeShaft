package com.naronco.cubeshaft.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.naronco.cubeshaft.KeyManager;

public class MenuControls extends Menu 
{
	private int selected = -1;
	
	@Override
	public void init() 
	{
		buttons.add(new Button(0, (width-300)/2 +200, height-40 , 300, 30, "Done"));
	}
	
	@Override
	protected void buttonClicked(Button button) 
	{
		if(button.id == 0)
		{
			game.setMenu(new StartMenu());
		}
	}
	
	@Override
	public void render(int xm, int ym) 
	{
		fill(0, 0, width, height, 0xff442222);
			
		int h = 80;	
		
		for(int i=0;i<KeyManager.getNames().size();i++)
		{
			int y = h + 5 + i * 30;
			String name = KeyManager.getNames().get(i);
			String key = Keyboard.getKeyName(KeyManager.getKey(name));
			int x = (width - TextRenderer.getTextLength("X"))/2 + 80;
			boolean hover = x < xm && xm < x+TextRenderer.getTextLength("X") && y < ym && ym < y+20;
			
			if(Mouse.isButtonDown(0))
			{
				if(h+i*30 < ym && ym < h+(i+1)*30 && !hover)
					selected = i;
				else if(hover)
				{
					KeyManager.resetKey(name);
				}
			}
			
			drawString(name, width/2 - TextRenderer.getTextLength(name), y, 0xffffff);
			if(selected == i)
			{
				key = "???";
			}
			drawString(key, (width - TextRenderer.getTextLength(key))/2 + 35, y, KeyManager.isDefault(name) ? 0xffffbb : 0x88ffff);		
						
			drawString("X", x, y, KeyManager.isDefault(name) ? 0x999999 : (hover ? 0xff0000 : 0xff4444 ));
		}
		
		fill(0, 0, width, h, 0xff331111);
		fill(0, height-h, width, height, 0xff331111);
		
		super.render(xm, ym);
	}
	
	@Override
	protected void keyType(char c, int keyIndex) 
	{
		if(selected >= 0)
		{
			if (keyIndex == Keyboard.KEY_ESCAPE) 
			{
				selected = -1;
				return;
			}
			String name = KeyManager.getNames().get(selected);
			KeyManager.setKey(name, keyIndex);
			selected = -1;
		}
		else
		{
			super.keyType(c, keyIndex);
		}
	}
}
