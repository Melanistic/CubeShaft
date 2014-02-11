package com.naronco.cubeshaft.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.Random;

import org.lwjgl.opengl.Display;

import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TextureLoader;

public class ProgressMenu extends Menu 
{
	private String progressTitle, progressText;

	@Override
	public void init() {
	}

	@Override
	protected void buttonClicked(Button b) {
	}

	@Override
	public void render(int xMouse, int yMouse) {
		super.render(xMouse, yMouse);
	}

	public void setProgressTitle(String title) {
		this.progressTitle = title;
	}

	public void setProgressText(String text) {
		this.progressText = text;
	}

	public void setProgress(int progress) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Tesselator t = Tesselator.instance;
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, TextureLoader.load("/terrain.png", GL_NEAREST));

		t.begin();
		t.color(0x606060);
		int tileSize = 32;
		int w = width / tileSize + 1;
		int h = height / tileSize + 1;
		Random random = new Random(100);
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				int xd = Math.abs(x - w / 2);
				int yd = Math.abs(y - h / 2);
				int br = 255 - (int) (Math.sqrt(xd * xd + yd * yd) * 15);
				int col = br << 16 | br << 8 | br;
				t.color(col);
				Tile tile = Tile.dirt;
				if (random.nextInt(3) == 0) tile = Tile.stone;
				float u0 = (tile.texIndex % 16) / 16.0f;
				float u1 = u0 + 1.0f / 16.0f;
				float v0 = (tile.texIndex / 16) / 16.0f;
				float v1 = v0 + 1.0f / 16.0f;
				t.vertexUV(x * tileSize, y * tileSize, 0.0f, u0, v0);
				t.vertexUV(x * tileSize + tileSize, y * tileSize, 0.0f, u1, v0);
				t.vertexUV(x * tileSize + tileSize, y * tileSize + tileSize, 0.0f, u1, v1);
				t.vertexUV(x * tileSize, y * tileSize + tileSize, 0.0f, u0, v1);
			}
		t.end();

		glDisable(GL_TEXTURE_2D);
		if (progress >= 0) {
			int x = width / 2 - 100;
			int y = height / 2 + 32;

			t.begin();
			t.color(0x202020);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x, y + 6, 0.0f);
			t.vertex(x + 200, y + 6, 0.0f);
			t.vertex(x + 200, y + 4, 0.0f);
			t.color(0x808080);
			t.vertex(x, y, 0.0f);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x + 200, y + 4, 0.0f);
			t.vertex(x + 200, y, 0.0f);
			t.color(0x00CC00);
			t.vertex(x, y, 0.0f);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x + progress * 2, y + 4, 0.0f);
			t.vertex(x + progress * 2, y, 0.0f);
			t.end();
		}

		drawString(progressTitle, (width - TextRenderer.getTextLength(progressTitle)) / 2, height / 2 - 8 - 32, 0xffffff);
		drawString(progressText, (width - TextRenderer.getTextLength(progressText)) / 2, height / 2 - 8 + 16, 0xffffff);
		Display.update();
	}
}
