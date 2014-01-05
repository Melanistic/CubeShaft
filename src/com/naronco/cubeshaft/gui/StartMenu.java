package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.naronco.cubeshaft.Cubeshaft;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TextureLoader;

public class StartMenu extends Menu {
	@Override
	public void render(int xm, int ym) {
		GL11.glDisable(GL_TEXTURE_2D);
		fill(0, 0, width, height, 0xffddddff);
		super.render(xm, ym);
		GL11.glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/Cubeshaft.png", GL_NEAREST));
		Tesselator t = Tesselator.instance;
		GL11.glColor4f(1, 1, 1, 1);
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
					game.startUpWorld();
					this.game.levelManager.load(s, this.game.level,
							this.game.player);

					game.setInGame();
				}
			}
			drawString(s, (width - TextRenderer.getTextLength(s)) / 2,
					200 + 20 * i + 2, color);
		}
	}

	@Override
	public void input() {
		// TODO Auto-generated method stub
		super.input();
	}

	@Override
	public void init() {
		// buttons.add(new Button(1, (width-200)/2, 180, 200, 20, "World"));

	}

}
