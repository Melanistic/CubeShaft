package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.input.Mouse;

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
			this.game.setMenu(new LevelGenerateMenu());
			this.game.generateNewLevel();
		}
		if (b.id == 4) {
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
			int color = 0xffffff;
			if (200 + 15 * i < ym && ym < 200 + 20 * i + 20) {
				color = 0xffffffaa;
				if (Mouse.isButtonDown(0)) {
					this.game.levelManager.load(s, this.game.level, this.game.player);
					game.setInGame();
				}
			}
			//drawString(s, (width - TextRenderer.getTextLength(s)) / 2, 200 + 20 * i + 2, color);
		}
	}

	@Override
	public void input() {
		super.input();
	}
}
