package com.naronco.cubeshaft.gui;

public class MenuOptions extends Menu 
{
	@Override
	public void init() 
	{
		buttons.add(new Button(0, width/2, height/2, 200, 30, "Controls"));
	}
	
	@Override
	protected void buttonClicked(Button button) 
	{
		if(button.id == 0)
		{
			game.setMenu(new MenuControls());
		}
	}

}
