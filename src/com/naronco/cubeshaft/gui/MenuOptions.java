package com.naronco.cubeshaft.gui;

import java.util.ArrayList;
import java.util.List;

public class MenuOptions extends Menu 
{
	private List<Object[]> opt = new ArrayList<Object[]>();
	
	public void addControls()
	{
		opt.add(new Object[] {"Controls", new Runnable() 
		{			
			@Override
			public void run() 
			{
				game.setMenu(new MenuControls());
			}
		}});
		opt.add(new Object[] {"Player Name", new Runnable() 
		{
			@Override
			public void run() 
			{
				game.setMenu(new MenuPlayerEdit());
			}
		}});
	}
	
	@Override
	public void init() 
	{
		addControls();
		
		int butx = 300;
		int buty = 40;
		
		int spacex = 30;
		int spacey = 20;
		
		int maxrow = 5;
		int colums = (opt.size() / maxrow) + 1;
		
		
		for(int i=0;i<opt.size();i++)
		{		
			Object[] objs = opt.get(i);
					
			int colum = (i/maxrow);
			int row = i % maxrow;			
			
			int rows = opt.size() - (colum * maxrow) ;
			rows = rows > maxrow ? maxrow : rows;
						
			
			int xpos = ( (width/colums) - (butx+spacex) ) / 2 + (width/colums) * colum;
			int ypos = ( (height/rows) - (buty+spacey) ) / 2 + (buty+spacey) * row;
			
			buttons.add(new Button(i, xpos, ypos, butx, buty, (String)objs[0]));
		}
		//buttons.add(new Button(0, (width - 200)/2, height/2, 200, 30, "Controls"));
	}
	
	@Override
	protected void buttonClicked(Button button) 
	{
		Object[] objs = opt.get(button.id);
		if(objs[1] instanceof Runnable)
		{
			((Runnable)objs[1]).run();
		}
		/*
		if(button.id == 0)
		{
			game.setMenu(new MenuControls());
		}*/
	}
	
	@Override
	public void render(int xMouse, int yMouse) 
	{
		fill(0, 0, width, height, 0xffddddff);
		super.render(xMouse, yMouse);
	}

}
