package com.naronco.cubeshaft.gui;

import org.lwjgl.input.Keyboard;

public class MenuPlayerEdit extends Menu 
{
	Button b ;
	boolean edit = false;
	String name;
	
	@Override
	public void init() 
	{
		b = new Button(0, (width-400)/2, (height-40)/2+40, 400, 40, "Edit");
		buttons.add(b);
		buttons.add(new Button(1, (width-400)/2, (height-40)/2+100, 400, 40, "Done"));
	}
	
	@Override
	public void render(int xMouse, int yMouse) 
	{
		fill(0, 0, width, height, 0xffddddff);
		
		if(!edit)
			name = game.props.getProperty("player");
			
		drawString("Player Name:", (width-TextRenderer.getTextLength("Player Name:")) /2, height/3, 0xffffff);
		drawString(name + (edit?"_":""), (width - TextRenderer.getTextLength(name))/2, height/3+40, 0x00aaaa);
		super.render(xMouse, yMouse);
	}
	
	@Override
	protected void buttonClicked(Button button) 
	{
		if(button.id==0)
		{
			if(edit)
			{
				game.props.setProperty("player", name);
			}
			b.text = edit ? "Edit" : "OK";
			edit = !edit;
		}
		if(button.id==1)
		{
			game.setMenu(new StartMenu());
		}
	}
	
	@Override
	protected void keyType(char c, int keyIndex) 
	{
		if(edit)
		{
			if(keyIndex == Keyboard.KEY_BACK)
			{
				name = name.substring(0, name.length()-1);
			}
			else
				name+=c;
		}
		else
			super.keyType(c, keyIndex);
	}
}
