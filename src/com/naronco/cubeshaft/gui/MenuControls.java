package com.naronco.cubeshaft.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
			try {
			File dir = new File("data");
			dir.mkdir();
			File f = new File(dir,"config.dat");
			OutputStream out = new FileOutputStream(f);
			KeyManager.saveKeys(game.props);
			game.props.store(out, "");
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			game.setMenu(new StartMenu());
		}
	}
	
	@Override
	public void render(int xm, int ym) 
	{
		int max = 0;
		for(String key : KeyManager.getNames())
		{
			max = Math.max(max, TextRenderer.getTextLength( Keyboard.getKeyName(KeyManager.getKey(key))));
		}
	//	max+=10;
		
		fill(0, 0, width, height, 0xff442222);
			
		int h = 80;	
		
		for(int i=0;i<KeyManager.getNames().size();i++)
		{
			int y = h + 5 + i * 30;
			String name = KeyManager.getNames().get(i);
			String key = Keyboard.getKeyName(KeyManager.getKey(name));
			
			int x = (width - TextRenderer.getTextLength("X"))/2 + max + 15;
			
			boolean h_x = hover(xm,ym,x,h+i*30,x+TextRenderer.getTextLength("X")+10,h+(i+1)*30);
			boolean h_k = hover(xm,ym,width/2,h+i*30,x,h+(i+1)*30);
			
			if(Mouse.isButtonDown(0))
			{
				if(h_x)
					KeyManager.resetKey(name);
				
				else if(h_k)	
					selected = i;
				
			}
			
			drawString(name, width/2 - TextRenderer.getTextLength(name), y, 0xffffff);
			if(selected == i)
			{
				key = "???";
			}
			drawString(key, (width - TextRenderer.getTextLength(key))/2 + max/2 + 5, y, (KeyManager.isDefault(name) || selected == i) ? 0xffffbb : 0x88ffff);		
						
			drawString("X", x+5, y, KeyManager.isDefault(name) ? 0x999999 : (h_x ? 0xff0000 : 0xff4444 ));
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
	
	private boolean hover(int xm, int ym, int x0, int y0, int x1, int y1)
	{
		boolean h = (x0 < xm && xm < x1) && (y0 < ym && ym < y1);
		
		fill(x0+1, y0+1, x1-1, y1-1, h ?  0x66ffffff : 0x44ffffff);
		
		return h;
	}
}
