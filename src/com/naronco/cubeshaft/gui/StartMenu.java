package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.input.Mouse;

import com.naronco.cubeshaft.KeyManager;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TextureLoader;

public class StartMenu extends Menu {
	@Override
	public void init() {
		this.buttons.add(new Button(0, this.width / 2 - 200, this.height / 4, 400, 40, "Singleplayer"));
		this.buttons.add(new Button(1, this.width / 2 - 200, this.height / 4 + 60, 400, 40, "Multiplayer"));
		this.buttons.add(new Button(3, this.width / 2 - 200, this.height / 4 + 120, 400, 40, "Options"));
		this.buttons.add(new Button(4, this.width / 2 - 200, this.height / 4 + 180, 400, 40, "Quit"));
	}

	@Override
	protected void buttonClicked(Button b) {
		
		if (b.id == 0) {
		//	this.game.setMenu(new LevelGenerateMenu());
		//	this.game.generateNewLevel();
			this.game.setMenu(new MenuSelectWorld());
		}
		if(b.id == 3)
		{
			this.game.setMenu(new MenuOptions());
		}
		if (b.id == 4) 
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
			this.game.tryToQuit();
		}
	}

	@Override
	public void render(int xm, int ym) {
		glDisable(GL_TEXTURE_2D);
		fill(0, 0, width, height, 0xffddddff);
		super.render(xm, ym);
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, TextureLoader.load("/Cubeshaft.png", GL_NEAREST));
		Tesselator t = Tesselator.instance;
		glColor4f(1, 1, 1, 1);
		int x1 = (width - 660) / 2, y1 = 32, x2 = (width - 660) / 2 + 660, y2 = 160;
		t.begin();
		t.vertexUV(x1, y1, 0, 0, 0);
		t.vertexUV(x2, y1, 0, 1, 0);
		t.vertexUV(x2, y2, 0, 1, 1);
		t.vertexUV(x1, y2, 0, 0, 1);
		t.end();

	}

	@Override
	public void input() {
		super.input();
	}
}
