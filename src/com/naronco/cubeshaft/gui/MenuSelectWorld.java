package com.naronco.cubeshaft.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.input.Mouse;

import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.generator.LevelGenerator;
import com.naronco.cubeshaft.level.tile.Tile;

public class MenuSelectWorld extends Menu 
{	
	private int selected = -1;
	
	@Override
	public void init() 
	{
		this.buttons.add(new Button(0, (this.width - 200) / 2 - 300, 40, 200, 40, "New World"));
		this.buttons.add(new Button(1, (this.width - 200) / 2 + 0  , 40, 200, 40, "Delete World"));
		this.buttons.add(new Button(2, (this.width - 200) / 2 + 300, 40, 200, 40, "Play World"));
		
		this.buttons.add(new Button(3, (this.width - 200) / 2 - 300, 90, 200, 40, "Flat World"));
	}
	
	@Override
	protected void buttonClicked(Button button) 
	{
		if(button.isClickable)
		{
			if(button.id == 0)
			{
				this.game.setMenu(new LevelGenerateMenu());
				
				LevelGenerator generator = new LevelGenerator(this.game);
				Level level = new Level();
				generator.generate(getFreeWorldName("new world"), level, 512, 128, 512);
				this.game.levelManager.save(level.name, level, game.player);
				
				//this.game.generateNewLevel();
				this.game.setMenu(new MenuSelectWorld());
				
				
				
			}
			if(button.id==3)
			{
				Level level = new Level();
				int x = 256, y = 128, z = 256;
				byte[] b = new byte[x*y*z];	
				for(int j = 1;j<x;j++)
				{
					for(int l = 1;l<z;l++)
					{
						for(int k=0;k<5;k++)
						{
						////b[(x + l) * z + j] = (byte) Tile.stone.id;
							int i = (k * x + l) * z + j;
							if(i>=b.length||i<0)continue;
							System.out.println("setblock "+j+" "+k+" "+l);
							b[i] = 1;
						}
					}
						
				}
				level.init(getFreeWorldName("flatmap"), x, y, z, b);
				this.game.levelManager.save(level.name, level, game.player);
				this.game.setMenu(new MenuSelectWorld());
			}
			if(button.id == 2)
			{
				File[] f = new File("world").listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File arg0, String arg1) {
						return arg1.endsWith(".csworld");
					}
				});
				Arrays.sort(f, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				
				String s = f[selected].getName().replace(".csworld", "");
				this.game.levelManager.load(s, this.game.level, this.game.player);
				game.player.resetPos();
				game.setInGame();
			}
			if(button.id == 1)
			{
				File[] f = new File("world").listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File arg0, String arg1) {
						return arg1.endsWith(".csworld");
					}
				});
				Arrays.sort(f, new Comparator<File>() {
					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				
				f[selected].delete();
				selected = -1;
			}
			
		}
		
	}
	
	private String getFreeWorldName(String defWorld)
	{
		File worlds = new File("world");
		while(new File(worlds,defWorld+".csworld").exists())
			defWorld += "-";
		
		return defWorld;
	}
	
	@Override
	protected void keyType(char c, int keyIndex) 
	{
		super.keyType(c, keyIndex);
		
		
	}
	
	@Override
	public void render(int xm, int ym) 
	{
		fill(0, 0, width, height, 0xffddddff);
		
		super.render(xm, ym);
		
		File[] f = new File("world").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".csworld");
			}
		});
		Arrays.sort(f, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		for (int i = 0; i < f.length; i++) {
			String s = f[i].getName().replace(".csworld", "");
			if(i == selected)
			{
				s = "> " + s + " <";
			}
			int color = 0xffffff;
			if (200 + 15 * i < ym && ym < 200 + 20 * i + 20) 
			{
				color = 0xffffffaa;
				if (Mouse.isButtonDown(0)) 
				{
					selected = i;
				}
			}
			drawString(s, (width - TextRenderer.getTextLength(s)) / 2, 200 + 20 * i + 2, color);
			
		}
		if(selected > -1)
			fill(width/2 -30, 200 + 20 * selected, 0xffaaffaa, width/2+30,  200 + 20 * selected + 20);
	}
	
	@Override
	public void tick() 
	{
		buttons.get(1).isClickable = selected != -1;
		buttons.get(2).isClickable = selected != -1;
	}
}
